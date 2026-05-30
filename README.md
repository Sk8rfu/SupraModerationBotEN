<p align="center">
  <img src="https://github.com/Sk8rfu/SupraModerationBotEN/blob/assets/supra.jpg?raw=true?raw=true">
</p>

# SupraModerationBotEN

A powerful Discord moderation bot built with Java + JDA, designed to make server management fast, efficient, and reliable.

## 🚀 Installation & Setup

### 1. Install Required Software

Java 17+

Maven 3.8+

---
Check your versions:

```
java -version
mvn -version
```

### 2. Clone the Project
---

```
git clone https://github.com/Sk8rfu/SupraModerationBotEN.git
cd SupraModerationBotEN
```

### 3. Add Your Discord Token
---
Open:

```
src/main/java/com/mikubot/config/Config.java
```
Replace:

```
public static final String TOKEN = "YOUR_TOKEN_HERE";
```

## ⚠️ Important:
Never upload your real token to GitHub.
If you accidentally expose it, regenerate it immediately in the Discord Developer Portal.

### 4. Build the Project
---

Run the build command in the project root, where pom.xml is located:
```
pom.xml
```

```
cd SupraModerationBot
mvn clean package
```
After a successful build, Maven will generate the JAR file inside:

```
target/
```

### 5. Run the Bot
---
```
java -jar target/SupraModerationBot-1.0-SNAPSHOT.jar
```

### 📁 Project Structure
---
```
src/
 └── main/
     └── java/
         └── com/
             └── mikubot/
                 ├── commands/        # All bot commands
                 ├── config/          # Config.java (token)
                 ├── listeners/       # Event listeners
                 ├── util/            # Utility classes
                 ├── Main.java        # Main entry point
                 └── CommandManager.java
```

### 📜 Commands
---

### 🧾 Information

/ping	Shows latency

/help	Displays help menu

/userinfo	Shows user information

/serverinfo	Shows server information

/avatar	Displays user avatar

/about	Information about the bot

---

### 🔨 Moderation

/ban	Bans a user

/tempban	Temporary ban (1s, 1m, 1h, 1d, 1w)

/unban	Removes a ban

/banid	Bans a user by ID

/banlist	Shows banned users

/kick	Kicks a user

/mute	Mutes (timeout)

/unmute	Unmutes (timeout)

/muterole	Mutes via role

/unmuterole	Unmutes via role

/warn	Issues a warning

/unwarn	Clears all warnings

/warnings	Shows user warnings

---

### ⚙️ Management

/giverole	Gives a role

/removerole	Removes a role

/createrole	Creates a role

/editrole	Edits a role

/deleterole	Deletes a role

/clear	Deletes messages

/slowmode	Sets slowmode

/lock	Locks a channel

/unlock	Unlocks a channel

/nickname	Changes nickname

/invite	Creates an invite (1s, 1m, 1h, 1d, 1w)

---

### 👥 Social

/report	Sends a report to moderators

/suggest	Sends a suggestion

---

### ❤️ Made with Love
### This bot was created with passion, dedication, and ❤️ to help every Discord server stay safe, organized, and enjoyable.
