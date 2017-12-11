package dycode.com.commu.data.dto

/**
 * Created by Asus on 12/2/2017.
 */
class MessageDto {

    var text: String? = null
    var iduser: String? = null
    var timestamp: Long? = null

    constructor(){}

    constructor(text: String?, iduser: String?, timestamp: Long?) {
        this.text = text
        this.iduser = iduser
        this.timestamp = timestamp
    }
}