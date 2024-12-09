
README for Running the Application

Prerequisites
1. Operating System: Windows (recommended).
2. Software:
   - Java JDK 8 or later: Install from Oracle's website.
   - JavaFX SDK: Download from Gluon.
   - MySQL: Install from MySQL's official website.
   - IntelliJ IDEA: Install from JetBrains.
   - Weka: Install from Weka's official website.

3. Database Setup:
   - MySQL database named 'echofeed'.
   - Import the provided 'articles' and 'users' tables.

Setting Up the Environment
1. Clone the Repository:
   - Copy the project files to your local machine.

2. Set Up Database Connection:
   - Update the DatabaseConnection class with your MySQL username and password.

3. Configure JavaFX:
   - Link JavaFX SDK in IntelliJ IDEA:
     - Go to File > Project Structure > Libraries.
     - Add the path to the JavaFX SDK's lib folder.

4. Add JVM Options for JavaFX:
   - Navigate to Run > Edit Configurations.
   - In VM options, add:
     --module-path <path_to_javafx_sdk>/lib --add-modules javafx.controls,javafx.fxml

5. Download Required Libraries:
   - Include weka.jar in your project's lib folder.

6. Load Resources:
   - Ensure all FXML files, CSS stylesheets, and images are in their respective folders.

7. Train the Recommendation Model:
   - Run the MLModelTrainer class to create a trained model file (ml_model.dat).

Running the Application
1. Start the Application:
   - Run the StartApplication class from IntelliJ IDEA.

2. Navigate Through the Application:
   - Use the interface for:
     - Sign Up/Login: For user access.
     - Admin Login: For administrative functions like managing articles and users.

3. Add Articles:
   - Use the Add Article feature to input article details and categorize them using the trained Weka model.

4. Fetch Articles:
   - Use the ArticleFetcher to fetch and save articles from an API.

5. View and Recommend Articles:
   - Logged-in users can view or get recommendations based on their preferences.

6. Manage Articles and Users:
   - Admin users can delete articles and users via their respective interfaces.

Troubleshooting
- Ensure MySQL server is running and accessible.
- Verify the path to JavaFX SDK and ensure Weka library is loaded.
- Check database credentials and table structure in MySQL.
- Resolve FXML-related errors by checking the resource file paths.

Feel free to reach out for additional support or clarification!
