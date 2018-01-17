package dycode.com.commu.data.dto

/**
 * Created by Asus on 12/2/2017.
 */
class MyroomDto {
    var iduser: String? = null
    var lastMessageKey: String? = null
    var lastTimestamp: Long? = null
    var unreadMessage: Long? = null

    constructor() {}
    constructor(iduser: String?, lastMessageKey: String?, lastTimeStamp: Long?, unreadMessage: Long?) {
        this.iduser = iduser
        this.lastMessageKey = lastMessageKey
        this.lastTimestamp = lastTimeStamp
        this.unreadMessage = unreadMessage
    }


}
