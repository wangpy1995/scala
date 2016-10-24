import com.sun.istack.internal.NotNull

/**
  * Created by w5921 on 2016/10/16.
  */
class Location {
  //地點ID
  var locId: Int
  //經度
  var latitude: Int
  //緯度
  var longitude: Int
  //城市
  var country: String
  //粗類別
  var coarseCat: String
  //細類別
  var fineCat: String

  def locId_(@NotNull locId: Int) = this.locId = locId

  def locId:Int = this.locId

  def latitude_(@NotNull latitude: Int):Unit = {
    this.latitude = latitude
  }

  def latitude:Int = this.latitude

  def longitude_(@NotNull longitude: Int):Unit = this.longitude = longitude

  def longitude:Int = this.longitude

  def country_(@NotNull country: String):Unit = this.country = country

  def country:String = this.country

  def coarseCat_(coarseCat: String):Unit = this.coarseCat = coarseCat

  def coarseCat:String = this.coarseCat

  def fineCat_(fineCat: String):Unit = this.fineCat = fineCat

  def fineCat:String = this.fineCat
}
