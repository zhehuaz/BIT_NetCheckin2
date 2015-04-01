# BIT_NetCheckin2

 Overview
---
 Auto login app of Android for Beijing Institute of Technology.
 Tested in Liangxiang Campus,available in all buildings covered by Intranet WIFI, custom
 AP as well.
     
 Features
---
*   Basic login and logout
*   Auto login when wifi is available.
*   Add custom SSID.

 Download
---
Now available in 

*   [Flyme Store](http://app.flyme.cn/apps/public/detail?package_name=org.bitnp.netcheckin2)

*   [Wandoujia](http://www.wandoujia.com/apps/org.bitnp.netcheckin2)

*   [Mi Store](http://app.mi.com/detail/90070)
    
*ATTENTION* New version is using Xiaomi States server, which may upload your device info and
error log to Xiaomi Server so as to help promote your experience.
Except that, any other info including BIT account won't be sent anywhere.

 Developers
---
Welcome to contribute to this app.Feel free to clone, fork, commit issues or write docs.

Present BUG includes:

1. In Buttion Notification, when a button is pressed, the notification won't be canceled(close),
even if isAutoCancel(true) is called.
2. Unable to show balance if user didn't logged in with this app(somewhere else like Browser).
Because I need UID to check balance, but I can't access it except from login response, 
and it's labile unfortunately.

THANKS a lot for helping fix them.

Any questions, contact me @ zhehuaxiao at gmail
or [zchang.me](http://zchang.me)
