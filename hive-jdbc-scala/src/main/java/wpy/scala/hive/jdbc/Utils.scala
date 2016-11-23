package wpy.scala.hive.jdbc

import java.net.URI
import java.sql.SQLException
import java.util.Properties
import java.util.regex.Pattern

import org.apache.hive.service.cli.HiveSQLException
import org.apache.hive.service.rpc.thrift.{TStatus, TStatusCode}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by w5921 on 2016/10/25.
  */
class Utils {

}

object Utils {
  private[jdbc] val LOG: Logger = LoggerFactory.getLogger(classOf[Utils].getName)
  private[jdbc] val URL_PREFIX: String = "jdbc:hive2://"
  private[jdbc] val DEFAULT_PORT: String = "10000"
  private[jdbc] val DEFAULT_DATABASE: String = "default"
  private val URI_JDBC_PREFIX: String = "jdbc:"
  private val URI_HIVE_PREFIX: String = "hive2:"
  private[jdbc] val HIVE_SERVER2_RETRY_KEY: String = "hive.server2.retryserver"
  private[jdbc] val HIVE_SERVER2_RETRY_TRUE: String = "true"
  private[jdbc] val HIVE_SERVER2_RETRY_FALSE: String = "false"

  @throws[SQLException]
  private[jdbc] def verifySuccessWithInfo(status: TStatus) = verifySuccess(status, withInfo = true)

  @throws[SQLException]
  private[jdbc] def verifySuccess(status: TStatus): Unit = verifySuccess(status, withInfo = false)

  @throws[SQLException]
  private[jdbc] def verifySuccess(status: TStatus, withInfo: Boolean): Unit = {
    if ((status.getStatusCode ne TStatusCode.SUCCESS_STATUS) && (!withInfo || (status.getStatusCode ne TStatusCode.SUCCESS_WITH_INFO_STATUS))) throw new HiveSQLException(status)
  }

  /**
    * Remove the deprecatedName param from the fromMap and put the key value in the toMap.
    * Also log a deprecation message for the client.
    *
    * @param fromMap
    * @param toMap
    * @param deprecatedName
    * @param newName
    * @param newUsage
    */
  private def handleParamDeprecation(fromMap: Map[String, String], toMap: Map[String, String], deprecatedName: String, newName: String, newUsage: String) = if (fromMap.contains(deprecatedName)) {
    LOG.warn("***** JDBC param deprecation *****")
    LOG.warn("The use of " + deprecatedName + " is deprecated.")
    LOG.warn("Please use " + newName + " like so: " + newUsage)
    fromMap ++ Map(newName -> fromMap.get(deprecatedName))
    fromMap - deprecatedName
  }

  private def joinStringArray(stringArray: Array[String], seperator: String): String = {
    val stringBuilder: StringBuilder = new StringBuilder
    var cur: Int = 0
    val end: Int = stringArray.length
    while (cur < end) {
      {
        if (cur > 0)
          stringBuilder.append(seperator)

        stringBuilder.append(stringArray(cur))
      }
      {
        cur += 1
        cur - 1
      }
    }
    stringBuilder.toString
  }

  private def configureConnParams(connParams: Utils.JdbcConnectionParams) {
    val serviceDiscoveryMode = connParams.sessionVars(JdbcConnectionParams.SERVICE_DISCOVERY_MODE)
    if ((serviceDiscoveryMode != null) &&
      JdbcConnectionParams.SERVICE_DISCOVERY_MODE_ZOOKEEPER.equalsIgnoreCase(serviceDiscoveryMode)) {
      // Set ZooKeeper ensemble in connParams for later use
      connParams.zooKeeperEnsemble_(joinStringArray(connParams.authorityList, ","))
      // Configure using ZooKeeper
      ZooKeeperHiveClientHelper.configureConnParams(connParams)
    }
    else {
      val authority: String = connParams.authorityList(0)
      val jdbcURI: URI = URI.create(URI_HIVE_PREFIX + "//" + authority)
      // Check to prevent unintentional use of embedded mode. A missing "/"
      // to separate the 'path' portion of URI can result in this.
      // The missing "/" common typo while using secure mode, eg of such url -
      // jdbc:hive2://localhost:10000;principal=hive/HiveServer2Host@YOUR-REALM.COM
      if (jdbcURI.getAuthority != null) {
        val host: String = jdbcURI.getHost
        var port: Int = jdbcURI.getPort
        if (host == null)
          throw new JdbcUriParseException("Bad URL format. Hostname not found " + " in authority part of the url: " + jdbcURI.getAuthority + ". Are you missing a '/' after the hostname ?", null)
        // Set the port to default value; we do support jdbc url like:
        // jdbc:hive2://localhost/db
        if (port <= 0) port = Utils.DEFAULT_PORT.toInt
        connParams.host_(jdbcURI.getHost)
        connParams.port_(jdbcURI.getPort)
      }
    }
  }

  private[jdbc] def parseURL(uri: String, info: Properties): Utils.JdbcConnectionParams = {
    val connParams: Utils.JdbcConnectionParams = new Utils.JdbcConnectionParams
    var uri1 = uri
    if (!uri.startsWith(URL_PREFIX)) throw new JdbcUriParseException("Bad URL format: Missing prefix jdbc:hive2://", null)

    if (uri.equalsIgnoreCase(URL_PREFIX)) {
      connParams isEmbeddedMode_ true
      return connParams
    }

    // The JDBC URI now supports specifying multiple host:port if dynamic service discovery is
    // configured on HiveServer2 (like: host1:port1,host2:port2,host3:port3)
    // We'll extract the authorities (host:port combo) from the URI, extract session vars, hive
    // confs & hive vars by parsing it as a Java URI.
    // To parse the intermediate URI as a Java URI, we'll give a dummy authority(dummy:00000).
    // Later, we'll substitute the dummy authority for a resolved authority.
    val dummyAuthorityString: String = "dummyhost:00000"
    val suppliedAuthorities: String = getAuthorities(uri, connParams)
    if ((suppliedAuthorities == null) || suppliedAuthorities.isEmpty) {
      // Given uri of the form:
      // jdbc:hive2:///dbName;sess_var_list?hive_conf_list#hive_var_list
      connParams.isEmbeddedMode_(true)
    } else {
      LOG.info("Supplied authorities: " + suppliedAuthorities)
      val authorityList: Array[String] = suppliedAuthorities.split(",")
      connParams.authorityList_(authorityList)
      uri1 = uri.replace(suppliedAuthorities, dummyAuthorityString)
    }
    val jdbcURI: URI = URI.create(uri.substring(URI_JDBC_PREFIX.length))

    // key=value pattern
    val pattern: Pattern = Pattern.compile("([^;]*)=([^;]*)[;]?")

    // dbname and session settings
    var sessVars: String = jdbcURI.getPath

    if ((sessVars != null) && !sessVars.isEmpty) {
      var dbName = ""
      // removing leading '/' returned by getPath()
      sessVars = sessVars.substring(1)
      if (!sessVars.contains(";")) {
        // only dbname is provided
        dbName = sessVars
      } else {
        // we have dbname followed by session parameters
        dbName = sessVars.substring(0, sessVars.indexOf(';'))
        sessVars = sessVars.substring(sessVars.indexOf(';') + 1)
        if (sessVars != null) {
          val sessMatcher = pattern.matcher(sessVars)
          while (sessMatcher.find()) {
            if ((connParams.sessionVars ++
              Map(sessMatcher.group(1) -> sessMatcher.group(2))
              ) != null) {
              throw new JdbcUriParseException("Bad URL format: Multiple values for property " + sessMatcher.group(1), null)
            }
          }
        }
      }
      if (!dbName.isEmpty) connParams.dbName_(dbName)
    }

    val confStr: String = jdbcURI.getQuery
    if (confStr != null) {
      val confMatcher = pattern.matcher(confStr)
      while (confMatcher.find)
        connParams.hiveConfs ++ Map(confMatcher.group(1) -> confMatcher.group(2))
    }

    val varStr: String = jdbcURI.getFragment
    if (varStr != null) {
      val varMatcher = pattern.matcher(varStr)
      while (varMatcher.find)
        connParams.hiveVars ++ Map(varMatcher.group(1) -> varMatcher.group(2))
    }

    // Apply configs supplied in the JDBC connection properties object
    while (info.keys.hasMoreElements) {
      info.keys().nextElement() match {
        case key: String =>
          if (key.startsWith(JdbcConnectionParams.HIVE_VAR_PREFIX))
            connParams.hiveVars ++ Map(key.substring(JdbcConnectionParams.HIVE_VAR_PREFIX.length) -> info.getProperty(key))
          else if (key.startsWith(JdbcConnectionParams.HIVE_CONF_PREFIX))
            connParams.hiveConfs ++ Map(key.substring(JdbcConnectionParams.HIVE_CONF_PREFIX.length) -> info.getProperty(key))
        case _ =>
      }
    }

    // Extract user/password from JDBC connection properties if its not supplied
    // in the connection URL
    if (!connParams.sessionVars.contains(JdbcConnectionParams.AUTH_USER)) {
      if (info.containsKey(JdbcConnectionParams.AUTH_USER))
        connParams.sessionVars ++ Map(JdbcConnectionParams.AUTH_USER -> info.getProperty(JdbcConnectionParams.AUTH_USER))
      if (info.containsKey(JdbcConnectionParams.AUTH_PASSWD))
        connParams.sessionVars ++ Map(JdbcConnectionParams.AUTH_PASSWD -> info.getProperty(JdbcConnectionParams.AUTH_PASSWD))
    }

    if (info.containsKey(JdbcConnectionParams.AUTH_TYPE))
      connParams.sessionVars ++ Map(JdbcConnectionParams.AUTH_TYPE -> info.getProperty(JdbcConnectionParams.AUTH_TYPE))

    // Handle all deprecations here:
    var newUsage: String = null
    val usageUrlBase: String = "jdbc:hive2://<host>:<port>/dbName;"
    // Handle deprecation of AUTH_QOP_DEPRECATED
    newUsage = usageUrlBase + JdbcConnectionParams.AUTH_QOP + "=<qop_value>"
    handleParamDeprecation(connParams.sessionVars, connParams.sessionVars,
      JdbcConnectionParams.AUTH_QOP_DEPRECATED, JdbcConnectionParams.AUTH_QOP, newUsage)

    // Handle deprecation of TRANSPORT_MODE_DEPRECATED
    newUsage = usageUrlBase + JdbcConnectionParams.TRANSPORT_MODE + "=<transport_mode_value>"
    handleParamDeprecation(connParams.hiveConfs, connParams.sessionVars,
      JdbcConnectionParams.TRANSPORT_MODE_DEPRECATED, JdbcConnectionParams.TRANSPORT_MODE, newUsage)

    // Handle deprecation of HTTP_PATH_DEPRECATED
    newUsage = usageUrlBase + JdbcConnectionParams.HTTP_PATH + "=<http_path_value>"
    handleParamDeprecation(connParams.hiveConfs, connParams.sessionVars,
      JdbcConnectionParams.HTTP_PATH_DEPRECATED, JdbcConnectionParams.HTTP_PATH, newUsage)

    if (connParams.isEmbeddedMode) {
      // In case of embedded mode we were supplied with an empty authority.
      // So we never substituted the authority with a dummy one.
      connParams.host_(jdbcURI.getHost)
      connParams.port_(jdbcURI.getPort)
    }
    else {
      // Configure host, port and params from ZooKeeper if used,
      // and substitute the dummy authority with a resolved one
      configureConnParams(connParams)
      // We check for invalid host, port while configuring connParams with configureConnParams()
      val authorityStr: String = connParams.host + ":" + connParams.port
      LOG.info("Resolved authority: " + authorityStr)
      connParams.jdbcUriString_(uri1.replace(dummyAuthorityString, authorityStr))
    }
    connParams
  }

  /**
    * Get the authority string from the supplied uri, which could potentially contain multiple
    * host:port pairs.
    *
    * @param uri
    * @param connParams
    * @return
    * @throws JdbcUriParseException
    */
  @throws[JdbcUriParseException]
  private def getAuthorities(uri: String, connParams: Utils.JdbcConnectionParams): String = {
    var authorities: String = null
    /**
      * For a jdbc uri like:
      * jdbc:hive2://<host1>:<port1>,<host2>:<port2>/dbName;sess_var_list?conf_list#var_list
      * Extract the uri host:port list starting after "jdbc:hive2://",
      * till the 1st "/" or "?" or "#" whichever comes first & in the given order
      * Examples:
      * jdbc:hive2://host1:port1,host2:port2,host3:port3/db;k1=v1?k2=v2#k3=v3
      * jdbc:hive2://host1:port1,host2:port2,host3:port3/;k1=v1?k2=v2#k3=v3
      * jdbc:hive2://host1:port1,host2:port2,host3:port3?k2=v2#k3=v3
      * jdbc:hive2://host1:port1,host2:port2,host3:port3#k3=v3
      */
    val fromIndex: Int = Utils.URL_PREFIX.length
    var toIndex: Int = -1
    val toIndexChars: List[String] = List[String]("/", "?", "#")
    var foundIndex = false
    for (toIndexChar <- toIndexChars; if foundIndex) {
      toIndex = uri.indexOf(toIndexChar, fromIndex)
      if (toIndex > 0) {
        foundIndex = true
      }
    }
    if (toIndex < 0) authorities = uri.substring(fromIndex)
    else authorities = uri.substring(fromIndex, toIndex)
    authorities
  }

  class JdbcConnectionParams {
    private var h: String = null
    private var p: Int = 0
    private val jdbcUri: String = null
    private val db: String = "default"
    private var hiveCon: Map[String, String] = Map()
    private var hiveVar: Map[String, String] = Map()
    private var sessionVar: Map[String, String] = Map()
    private var isEmbedded: Boolean = false
    private var authority: Array[String] = _
    private var zooKeeperEnsem: String = _
    private var currentHostZnode: String = _
    private var rejectedHostZnode: List[String] = List()

    def host = h

    def port = p

    def jdbcUriString = jdbcUri

    def dbName = db

    def hiveConfs = hiveCon

    def hiveVars = hiveVar

    def sessionVars = sessionVar

    def isEmbeddedMode = isEmbedded

    def authorityList = authority

    def zooKeeperEnsemble = zooKeeperEnsem

    def currentHostZnodePath = currentHostZnode

    def rejectedHostZnodePaths = rejectedHostZnode


    def host_(h: String) = this.h = h

    def port_(p: Int) = this.p = p

    def jdbcUriString_(jdbcUri: String) = this.jdbcUri

    def dbName_(db: String) = this.db

    def hiveConfs_(hiveCon: Map[String, String]) = this.hiveCon = hiveCon

    def hiveVars_(hiveVar: Map[String, String]) = this.hiveVar = hiveVar

    def sessionVars_(sessionVar: Map[String, String]) = this.sessionVar = sessionVar

    def isEmbeddedMode_(isEmbedded: Boolean) = this.isEmbedded = isEmbedded

    def authorityList_(authority: Array[String]) = this.authority = authority

    def zooKeeperEnsemble_(zooKeeperEnsem: String) = this.zooKeeperEnsem = zooKeeperEnsem

    def currentHostZnodePath_(currentHostZnode: String) = this.currentHostZnode = currentHostZnode

    def rejectedHostZnodePaths(rejectedHostZnodes: List[String]) = this.rejectedHostZnode = rejectedHostZnodes
  }

  object JdbcConnectionParams {
    private[jdbc] val RETRIES: String = "retries"
    private[jdbc] val AUTH_TYPE: String = "auth"
    private[jdbc] val AUTH_QOP_DEPRECATED: String = "sasl.qop"
    private[jdbc] val AUTH_QOP: String = "saslQop"
    private[jdbc] val AUTH_SIMPLE: String = "noSasl"
    private[jdbc] val AUTH_TOKEN: String = "delegationToken"
    private[jdbc] val AUTH_USER: String = "user"
    private[jdbc] val AUTH_PRINCIPAL: String = "principal"
    private[jdbc] val AUTH_PASSWD: String = "password"
    private[jdbc] val AUTH_KERBEROS_AUTH_TYPE: String = "kerberosAuthType"
    private[jdbc] val AUTH_KERBEROS_AUTH_TYPE_FROM_SUBJECT: String = "fromSubject"
    private[jdbc] val ANONYMOUS_USER: String = "anonymous"
    private[jdbc] val ANONYMOUS_PASSWD: String = "anonymous"
    private[jdbc] val USE_SSL: String = "ssl"
    private[jdbc] val SSL_TRUST_STORE: String = "sslTrustStore"
    private[jdbc] val SSL_TRUST_STORE_PASSWORD: String = "trustStorePassword"
    private[jdbc] val TRANSPORT_MODE_DEPRECATED: String = "hive.server2.transport.mode"
    private[jdbc] val TRANSPORT_MODE: String = "transportMode"
    private[jdbc] val HTTP_PATH_DEPRECATED: String = "hive.server2.thrift.http.path"
    private[jdbc] val HTTP_PATH: String = "httpPath"
    private[jdbc] val SERVICE_DISCOVERY_MODE: String = "serviceDiscoveryMode"
    private[jdbc] val SERVICE_DISCOVERY_MODE_NONE: String = "none"
    private[jdbc] val SERVICE_DISCOVERY_MODE_ZOOKEEPER: String = "zooKeeper"
    private[jdbc] val ZOOKEEPER_NAMESPACE: String = "zooKeeperNamespace"
    private[jdbc] val ZOOKEEPER_DEFAULT_NAMESPACE: String = "hiveserver2"
    private[jdbc] val COOKIE_AUTH: String = "cookieAuth"
    private[jdbc] val COOKIE_AUTH_FALSE: String = "false"
    private[jdbc] val COOKIE_NAME: String = "cookieName"
    private[jdbc] val DEFAULT_COOKIE_NAMES_HS2: String = "hive.server2.auth"
    private[jdbc] val HTTP_HEADER_PREFIX: String = "http.header."
    private[jdbc] val FETCH_SIZE: String = "fetchSize"
    private[jdbc] val USE_TWO_WAY_SSL: String = "twoWay"
    private[jdbc] val TRUE: String = "true"
    private[jdbc] val SSL_KEY_STORE: String = "sslKeyStore"
    private[jdbc] val SSL_KEY_STORE_PASSWORD: String = "keyStorePassword"
    private[jdbc] val SSL_KEY_STORE_TYPE: String = "JKS"
    private[jdbc] val SUNX509_ALGORITHM_STRING: String = "SunX509"
    private[jdbc] val SUNJSSE_ALGORITHM_STRING: String = "SunJSSE"
    private[jdbc] val SSL_TRUST_STORE_TYPE: String = "JKS"
    private[jdbc] val HIVE_VAR_PREFIX: String = "hivevar:"
    private[jdbc] val HIVE_CONF_PREFIX: String = "hiveconf:"
  }

}