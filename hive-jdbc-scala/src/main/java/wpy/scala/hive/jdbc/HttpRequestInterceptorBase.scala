package wpy.scala.hive.jdbc

import java.io.IOException

import org.apache.http.client.CookieStore
import org.apache.http.protocol.HttpContext
import org.apache.http.{HttpException, HttpRequest, HttpRequestInterceptor}

/**
  * Created by w5921 on 2016/10/25.
  */
abstract class HttpRequestInterceptorBase(cookieStore: CookieStore, cookieName: String, isSSL: Boolean, additionalHeaders: Map[String, String]) extends HttpRequestInterceptor {

  private val isCookieEnabled = cookieStore != null

  @throws[HttpException][IOException]
  override def process(httpRequest: HttpRequest, httpContext: HttpContext): Unit = {
    if (this.cookieStore != null)
      httpContext.setAttribute("http.cookie-store", this.cookieStore)

    if (!this.isCookieEnabled ||
      httpContext.getAttribute("hive.server2.retryserver") == null &&
        (this.cookieStore == null || this.cookieStore != null && Utils.needToSendCredentials(this.cookieStore, this.cookieName, this.isSSL)) ||
      httpContext.getAttribute("hive.server2.retryserver") != null &&
        httpContext.getAttribute("hive.server2.retryserver") == "true") {
      this.addHttpAuthHeader(httpRequest, httpContext)
    }

    if (this.isCookieEnabled)
      httpContext.setAttribute("hive.server2.retryserver", "false")

    if (this.additionalHeaders != null) {
      val e = this.additionalHeaders.iterator

      while (e.hasNext) {
        val entry = e.next
        httpRequest.addHeader(entry._1, entry._2)
      }
    }
  }

  @throws[Exception]
  protected def addHttpAuthHeader(var1: HttpRequest, var2: HttpContext)
}