# **GitHub User Activity**
In this project, you will build a simple command line interface (CLI) to fetch the recent activity of a GitHub user and display it in the terminal.

## **Installation**

### **Clone the repository**
```bash
git clone https://github.com/CincoFolha/github-activity.git
cd github-activity
```

### **Build the project**
Using Gradle Wrapper:
```bash
./gradlew build
```
or no Windows: 
```bash
./gradlew.bat build
```
### **Run the application**
```bash
./gradlew run
```

## **Usage**
Provide the GitHub username as an argument when running the CLI
```bash
./gradlew run --args="username"
```
Display the fetched activity in the terminal
```bash
Output:
- Pushed 3 commits to username/developer-roadmap
- Opened a new issue in username/developer-roadmep
- Starred username/developer-roadmap
- ...
```

## **Technologies Used**
- Java
- Gradle
- org.json (to manipulate json)

## **URL**
https://roadmap.sh/projects/github-user-activity
