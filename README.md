# Commu
**Commu** is a regular chat app with basic functionality; send private messages to other user. Built using **Model-View-Presenter (MVP)** Structure, using **Kotlin** as **main** language and **Java** as **support**, **Firebase** as realtime database for the chatting system, including **Retrofit** and **RxJava** for networking.

The **Firebase** database Schema :
```
-chatrooms
  --[ID_USER]
    --[ID_ROOM]
      --iduser: [OTHER_ID_USER]
      --lastMessageKey: [refer to last pushed key in node messages inside corresponding ID_ROOM]   
      --lastTimeStamp : [ServerValue.TIMESTAMP]
      --unreadMessage : 0

-fcmtokens
  --[ID_USER]
    --token: [firebase cloud messaging token]
    
-messages
  --[ID_ROOM]
    --[PUSH_KEY]
      --iduser: [ID_USER who sent the message]
      --text : "hello world"
      --timestamp : [ServerValue.TIMESTAMP]
      
--users
  --[ID_USER]
    --fullname: ""
    --photo: ""
    --username: ""
```


## Current minus feature :
  - cannot upload image (on send message) yet.
  - select image from gallery (to change profile picture) script only work for xiaomi's phone. other than that will result in crash.


