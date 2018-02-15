# BoxFinder

**V V V V V English below V V V V V**

תרגישו חופשי להשתמש במה שתמצאו לנכון. זה אמור לעבוד אם טווח הצבעים מוגדר כמו שצריך.

בתיאוריה, נותן זווית לחפץ המרובע הכי קרוב (נקבע על ידי גודל) בתוך טווח צבעים מסויים, ושולח את זה לכתובת IP מוגדרת מראש.


מה נותר לעשות:

- **להגדיר את טווח הHSV המתאים בשביל חפצים צהובים**

- לשלוח זווית במקום קוארדינטות (זיהוי מרחק לא עובד כל כך טוב)

- לבדוק על רובוט

על מנת שנתונים יתקבלו, השרת(ים) צריכים לקבל את הפורטים: 42069, 6969, 666. 42069 בשביל פקודות, 6969 בשביל וידיאו ו6666 בשביל דיבוג (לא השתמשתי בזה עדיין אבל אפשר לגשת לזה בקלות דרך dconn.sent("your desired string") בקובץ runner.py)

**V V V V V English below V V V V V**



Feel free to reuse whatever you may find useful in this code. This is supposed to be functional if the colors range is set correctly.

In theory, detects the closest rectangular object that is colored within a pre defined color range, and sends a string to a user defined IP address with the appropriate angle


TODO:

- **Define the correct HSV range in worker.py for yellow objects**

- Send angle instead of coordinates (distance detection doesn't work very well)

- Test on a real robot


For data to be accepted, the server(s) need(s) to accept ports: 42069, 6969, 6666. 42069 for commands, 6969 for video and 6666 for debugging (not implemented yet but can be accessed simply by using dconn.sent("your desired string") in runner.py)
