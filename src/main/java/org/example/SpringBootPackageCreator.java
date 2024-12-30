    package org.example;

    import com.google.gson.Gson;
    import com.google.gson.reflect.TypeToken;

    import javax.swing.*;
    import java.awt.*;
    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.ArrayList;import java.util.List;

    public class SpringBootPackageCreator extends JFrame {
        private JTextField packageNameField, projectPathField;
        private JCheckBox dtoCheckBox, entityCheckBox, controllerCheckBox, repositoryCheckBox, configCheckBox, exceptionCheckBox, utilCheckBox, enumCheckBox, serviceCheckBox;
        private JCheckBox businessCheckBox, webclientCheckBox, helperCheckBox, implCheckBox;
        private JButton createButton, browseButton, deletePathButton, templatesButton;
        private static final String PATHS_FILE = "paths.json";
        private JComboBox<String> pathComboBox;


        public SpringBootPackageCreator() {
            setTitle("Spring Boot Paket Oluşturucu by ACY");
            setSize(950, 680);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            setResizable(true);




            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {
            }

            JPanel mainPanel = new JPanel(new GridLayout(5, 1, 9, 9));
            mainPanel.setBackground(new Color(113, 112, 112, 255));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));

            projectPathField = new JTextField();
            projectPathField.setBorder(BorderFactory.createTitledBorder("Proje Dosya Konumu: Örnek 'C:/Users/Proje'"));

            pathComboBox = new JComboBox<>();
            pathComboBox.setBorder(BorderFactory.createTitledBorder("Proje Dosya Konumu"));
            pathComboBox.addActionListener(e -> {
                String selectedPath = (String) pathComboBox.getSelectedItem();
                if (selectedPath != null) {
                    System.out.println("Seçilen Path: " + selectedPath);
                }
            });


            browseButton = new JButton("Dosya Seç");
            browseButton.setBackground(new Color(0, 246, 216, 255));
            browseButton.addActionListener(e -> openFileChooser());

            // Create the "Templateler" button
            templatesButton = new JButton("Templateler");
            templatesButton.setBackground(new Color(0, 246, 216, 255));
            templatesButton.addActionListener(e -> openTemplatesFolder());

            deletePathButton = new JButton("Seçili Path'i Sil");
            deletePathButton.setBackground(new Color(159, 24, 24, 255));
            deletePathButton.addActionListener(e -> deleteSelectedPath());


            JPanel pathPanel = new JPanel();
            pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.X_AXIS));
            //pathPanel.add(projectPathField);
            pathPanel.add(Box.createHorizontalStrut(10));
            pathPanel.add(pathComboBox);
            pathPanel.add(Box.createHorizontalStrut(10));
            pathPanel.add(browseButton);
            pathPanel.add(Box.createHorizontalStrut(10));
            pathPanel.add(deletePathButton);

            mainPanel.add(pathPanel);

            loadPaths();
            setVisible(true);

            packageNameField = new JTextField();
            packageNameField.setBorder(BorderFactory.createTitledBorder("Paket adı: Örnek 'dilekcelerim'            Not:Paket adı bir kelimdeden fazla olacaksa aralarına '_' işareti konulmalı. Örnek: is_birakma_bildirimi"));
            mainPanel.add(packageNameField);

            JPanel mainPackagePanel = new JPanel(new GridLayout(2, 8, 5, 5));
            dtoCheckBox = new JCheckBox("DTO");
            entityCheckBox = new JCheckBox("Entity");
            controllerCheckBox = new JCheckBox("Controller");
            repositoryCheckBox = new JCheckBox("Repository");
            serviceCheckBox = new JCheckBox("Service");
            configCheckBox = new JCheckBox("Config");
            exceptionCheckBox = new JCheckBox("Exception");
            utilCheckBox = new JCheckBox("Util");
            enumCheckBox = new JCheckBox("Enum");
            mainPackagePanel.add(dtoCheckBox);
            mainPackagePanel.add(entityCheckBox);
            mainPackagePanel.add(controllerCheckBox);
            mainPackagePanel.add(repositoryCheckBox);
            mainPackagePanel.add(serviceCheckBox);
            mainPackagePanel.add(configCheckBox);
            mainPackagePanel.add(exceptionCheckBox);
            mainPackagePanel.add(utilCheckBox);
            mainPackagePanel.setBorder(BorderFactory.createTitledBorder("Ana Paketler"));
            mainPackagePanel.add(enumCheckBox);
            mainPanel.add(mainPackagePanel);


            JPanel serviceSubPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            businessCheckBox = new JCheckBox("Business");
            webclientCheckBox = new JCheckBox("WebClient");
            serviceSubPanel.add(businessCheckBox);
            serviceSubPanel.add(webclientCheckBox);
            serviceSubPanel.setBorder(BorderFactory.createTitledBorder("Service Alt Paketler"));
            mainPanel.add(serviceSubPanel);

            JPanel webclientSubPanel = new JPanel(new GridLayout(1, 2, 10, 10));
            helperCheckBox = new JCheckBox("Helper");
            implCheckBox = new JCheckBox("Impl");
            webclientSubPanel.add(helperCheckBox);
            webclientSubPanel.add(implCheckBox);
            webclientSubPanel.setBorder(BorderFactory.createTitledBorder("WebClient Alt Paketler"));
            mainPanel.add(webclientSubPanel);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            createButton = new JButton("Paketi Oluştur");
            createButton.setFont(new Font("Arial", Font.BOLD, 16));
            createButton.setBackground(new Color(21, 133, 21));
            createButton.setForeground(Color.WHITE);
            createButton.setFocusPainted(false);
            buttonPanel.add(createButton);
            add(buttonPanel, BorderLayout.SOUTH);
            buttonPanel.add(templatesButton);
            add(buttonPanel, BorderLayout.SOUTH);
            add(mainPanel, BorderLayout.CENTER);

            createButton.addActionListener(e -> {
                String packageName = packageNameField.getText().trim();
                String projectPath = (String) pathComboBox.getSelectedItem();
                if (packageName.isEmpty() || projectPath == null || projectPath.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Paket adı ve proje dosya konumunu girin.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sanitizedPackageName = sanitizePackageName(packageName); // Paket adı temizleme
                String className = formatClassName(sanitizedPackageName);

                try {
                    createSpringBootPackageStructure(packageName, projectPath);
                    JOptionPane.showMessageDialog(null, "Paket yapısı başarıyla oluşturuldu!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Paket oluşturma hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            });
            serviceCheckBox.addActionListener(e -> toggleServiceOptions(serviceCheckBox.isSelected()));
            webclientCheckBox.addActionListener(e -> toggleWebClientOptions(webclientCheckBox.isSelected()));
        }
        private void openTemplatesFolder() {
            // Define the path to the template folder
            String templateFolderPath = "src/main/resources/templates/";

            // Open the folder in the default file explorer
            File templateFolder = new File(templateFolderPath);
            if (templateFolder.exists() && templateFolder.isDirectory()) {
                // Open file explorer on the template folder
                try {
                    Desktop.getDesktop().open(templateFolder);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Template klasörü açılamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Template klasörü bulunamadı: " + templateFolderPath, "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }


        private String sanitizePackageName(String packageName) {
            return packageName.replace("_", "").toLowerCase();
        }

        private String formatClassName(String packageName) {
            String[] words = packageName.split("_");
            StringBuilder className = new StringBuilder();
            for (String word : words) {
                className.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
            return className.toString();
        }


        private void openFileChooser() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String selectedPath = selectedFile.getAbsolutePath();
                pathComboBox.addItem(selectedPath);
                savePath(selectedPath);
            }
        }

        private void deleteSelectedPath() {
            String selectedPath = (String) pathComboBox.getSelectedItem();
            if (selectedPath != null) {
                pathComboBox.removeItem(selectedPath);
                removePathFromFile(selectedPath);
            }
        }

        private void savePath(String path) {
            List<String> paths = loadPathsFromFile();
            if (!paths.contains(path)) {
                paths.add(path);
                try (FileWriter writer = new FileWriter(PATHS_FILE)) {
                    new Gson().toJson(paths, writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void removePathFromFile(String path) {
            List<String> paths = loadPathsFromFile();
            paths.remove(path);
            try (FileWriter writer = new FileWriter(PATHS_FILE)) {
                new Gson().toJson(paths, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void loadPaths() {
            List<String> paths = loadPathsFromFile();
            for (String path : paths) {
                pathComboBox.addItem(path);
            }
        }

        private List<String> loadPathsFromFile() {
            try {
                if (Files.exists(Paths.get(PATHS_FILE))) {
                    String json = Files.readString(Paths.get(PATHS_FILE));
                    return new Gson().fromJson(json, new TypeToken<List<String>>() {
                    }.getType());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }

        private void toggleServiceOptions(boolean enabled) {
            businessCheckBox.setEnabled(enabled);
            webclientCheckBox.setEnabled(enabled);
            if (!enabled) {
                webclientCheckBox.setSelected(false);
                toggleWebClientOptions(false);
            }
        }

        private void toggleWebClientOptions(boolean enabled) {
            helperCheckBox.setEnabled(enabled);
            implCheckBox.setEnabled(enabled);
            if (!enabled) {
                helperCheckBox.setSelected(false);
                implCheckBox.setSelected(false);
            }
        }


        private void createSpringBootPackageStructure(String basePackageName, String projectPath) {
            File baseDir = new File(projectPath, sanitizePackageName(basePackageName).replace('.', '/'));

            // Create base package directory
            if (!baseDir.exists()) {
                baseDir.mkdirs();
            }

            // Create main packages and their placeholder classes
            if (dtoCheckBox.isSelected()) {
                createPlaceholderClass(baseDir, "dto", basePackageName, "RequestDto");
                createPlaceholderClass(baseDir, "dto", basePackageName, "ResponseDto");
            }
            if (entityCheckBox.isSelected()) {
                createPlaceholderClass(baseDir, "entity", basePackageName, "Entity");
            }
            if (controllerCheckBox.isSelected()) {
                createPlaceholderClass(baseDir, "controller", basePackageName, "Controller");
            }
            if (repositoryCheckBox.isSelected()) {
                createPlaceholderClass(baseDir, "repository", basePackageName, "Repository");
            }
            if (configCheckBox.isSelected()) {
                createPlaceholderClass(baseDir, "config", basePackageName, "Config");
            }
            if (exceptionCheckBox.isSelected()) {
                createPlaceholderClass(baseDir, "exception", basePackageName, "Exception");
            }
            if (utilCheckBox.isSelected()) {
                createPlaceholderClass(baseDir, "util", basePackageName, "Util");
            }
            if(enumCheckBox.isSelected()) {
                createPlaceholderClass(baseDir, "enums", basePackageName, "Enum");
            }



            // Service-specific logic
            if (serviceCheckBox.isSelected()) {
                File serviceDir = createPlaceholderClass(baseDir, "service", basePackageName, "Service");

                if (businessCheckBox.isSelected()) {
                    File businessDir = createPlaceholderClass(serviceDir, "business", basePackageName, "BusinessImpl");
                }

                if (webclientCheckBox.isSelected()) {
                    File webclientDir = createPlaceholderClass(serviceDir, "webclient", basePackageName, "WebClient");

                    if (helperCheckBox.isSelected()) {
                        createPlaceholderClass(webclientDir, "helper", basePackageName, "Helper");
                    }
                    if (implCheckBox.isSelected()) {
                        createPlaceholderClass(webclientDir, "impl", basePackageName, "Impl");
                    }
                }

            }
        }

        private File createPlaceholderClass(File parentDir, String packageName, String basePackageName, String classType) {
            File packageDir = packageName.isEmpty() ? parentDir : new File(parentDir, packageName.replace('.', File.separatorChar));

            if (!packageDir.exists()) {
                packageDir.mkdirs();
            }

            String capitalizedBaseName = capitalizeWords(basePackageName);
            String className = switch (classType) {
                case "Helper" -> capitalizedBaseName + "WebClientHelper";
                case "Impl" -> capitalizedBaseName + "WebClientImpl";
                case "Service" -> capitalizedBaseName + "Service";
                case "RequestDto" -> capitalizedBaseName + "RequestDTO";
                case "ResponseDto" -> capitalizedBaseName + "ResponseDTO";
                default -> capitalizedBaseName + classType;
            };

            File classFile = new File(packageDir, className + ".java");
            try {
                if (!classFile.exists()) {
                    classFile.createNewFile();
                    String fullPackageName = sanitizePackageName(getFullPackageName(packageDir));
                    String classContent = generatePlaceholderClassContent(classType, fullPackageName, className, capitalizedBaseName);
                    Files.writeString(classFile.toPath(), classContent);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return packageDir;
        }


        private String getFullPackageName(File packageDir) {
            // Proje kökünden itibaren src/main/java kısmını atla ve dosya yolunu paket ismine çevir.
            String fullPath = packageDir.getAbsolutePath().replace(File.separatorChar, '.');
            int srcIndex = fullPath.indexOf("src.main.java.");
            if (srcIndex != -1) {
                return fullPath.substring(srcIndex + "src.main.java.".length());
            }
            return "";
        }

        private String capitalizeFirstLetter(String input) {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }

        // Her kelimenin ilk harfini büyüten ve birleştiren metot
        private String capitalizeWords(String input) {
            String[] words = input.split("_");
            StringBuilder capitalized = new StringBuilder();
            for (String word : words) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
            return capitalized.toString();
        }
        private String loadTemplateContent(String templateName) {
            try {
                // template dizinindeki dosyayı okuyun
                String templatePath = "src/main/resources/templates/" + templateName + ".txt";
                Path path = Paths.get(templatePath);
                return Files.readString(path); // Dosyayı okuma işlemi
            } catch (IOException e) {
                e.printStackTrace();
                return null; // Hata durumunda null döner
            }
        }
        private String generatePlaceholderClassContent(
                String templateName,
                String packageName,
                String className,
                String basePackageName
        ) {
            // Load template content for "WebClientImpl"
            String templateFile = switch (templateName) {
                case "WebClientImpl" -> "WebClientImpl"; // Ensure this is the correct template file name
                default -> templateName;
            };

            String templateContent = loadTemplateContent(templateFile);
            if (templateContent == null) {
                return ""; // Return empty if template not found
            }

            // Generate DTO class names based on base package name
            String requestDtoClass = capitalizeWords(basePackageName) + "RequestDto";
            String responseDtoClass = capitalizeWords(basePackageName) + "ResponseDto";

            // Replace placeholders in template
            return templateContent
                    .replace("{{packageName}}", packageName)
                    .replace("{{className}}", className)
                    .replace("{{classNameLowercase}}", className.toLowerCase())
                    .replace("{{basePackageName}}", basePackageName)
                    .replace("{{requestDtoClass}}", requestDtoClass)
                    .replace("{{responseDtoClass}}", responseDtoClass);
        }


        private static void setUIFont(Font font) {
            java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof Font) {
                    UIManager.put(key, font);
                }
            }
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                setUIFont(new Font("New Times Romance", Font.PLAIN, 14)); // Font büyüklüğünü ayarlayın
                SpringBootPackageCreator app = new SpringBootPackageCreator();
                app.setVisible(true);
            });
            try {
                Path path = Paths.get("src/main/resources/templates/Controller.txt");
                String content = Files.readString(path);
                System.out.println("Dosya içeriği: " + content);
            } catch (Exception e) {
                System.err.println("Dosya erişim hatası: " + e.getMessage());
            }
        }

    }
