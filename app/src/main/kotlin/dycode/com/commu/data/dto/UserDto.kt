package dycode.com.commu.data.dto

/**
 * Created by Asus on 12/2/2017.
 */
class UserDto {
    var fullname: String? = null
    var photo: String? = null
    var username: String? = null

    constructor() {}
    constructor(fullname: String?, photo: String?, username: String?) {
        this.fullname = fullname
        this.photo = photo
        this.username = username
    }
}