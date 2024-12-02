package org.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SpringBootPackageCreator extends JFrame {
    private JTextField packageNameField, projectPathField;
    private JCheckBox dtoCheckBox, entityCheckBox, controllerCheckBox, repositoryCheckBox, serviceCheckBox, configCheckBox, exceptionCheckBox, utilCheckBox;
    private JCheckBox businessCheckBox, webclientCheckBox, helperCheckBox, implCheckBox;
    private JButton createButton, browseButton, deletePathButton;
    private static final String PATHS_FILE = "paths.json";
    private JComboBox<String> pathComboBox;

    public SpringBootPackageCreator() {
        setTitle("Spring Boot Paket Oluşturucu by ACY");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(true);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        mainPanel.setBackground(new Color(130, 137, 149, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        browseButton.setBackground(new Color(0, 255, 224, 255));
        browseButton.addActionListener(e -> openFileChooser());

        deletePathButton = new JButton("Seçili Path'i Sil");
        deletePathButton.setBackground(new Color(237, 37, 37, 255));
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
        packageNameField.setBorder(BorderFactory.createTitledBorder("Paket adı: Örnek 'araciliksozlesme'"));
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
        mainPackagePanel.add(dtoCheckBox);
        mainPackagePanel.add(entityCheckBox);
        mainPackagePanel.add(controllerCheckBox);
        mainPackagePanel.add(repositoryCheckBox);
        mainPackagePanel.add(serviceCheckBox);
        mainPackagePanel.add(configCheckBox);
        mainPackagePanel.add(exceptionCheckBox);
        mainPackagePanel.add(utilCheckBox);
        mainPackagePanel.setBorder(BorderFactory.createTitledBorder("Ana Paketler"));
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

        add(mainPanel, BorderLayout.CENTER);

        createButton.addActionListener(e -> {
            String packageName = packageNameField.getText().trim();
            String projectPath = (String) pathComboBox.getSelectedItem();
            if (packageName.isEmpty() || projectPath == null || projectPath.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Paket adı ve proje dosya konumunu girin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
                return new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
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
        File baseDir = new File(projectPath, basePackageName.replace('.', '/'));

        // Create base package directory
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        // Create main packages and their placeholder classes
        if (dtoCheckBox.isSelected()) {
            createPlaceholderClass(baseDir, "dto", basePackageName, "Dto");
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

        // Service-specific logic
        if (serviceCheckBox.isSelected()) {
            File serviceDir = createPlaceholderClass(baseDir, "service", basePackageName, "Service");

            if (businessCheckBox.isSelected()) {
                createPlaceholderClass(serviceDir, "business", basePackageName, "Business");
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
        File packageDir = new File(parentDir, packageName);
        if (!packageDir.exists()) {
            packageDir.mkdirs();
        }

        String className = capitalizeFirstLetter(basePackageName) + classType;
        File classFile = new File(packageDir, className + ".java");

        try {
            if (!classFile.exists()) {
                classFile.createNewFile();

                // **Tam Paket Adını Hesapla:**
                String fullPackageName = getFullPackageName(packageDir);

                // **Dosya İçeriğini Oluştur ve Yaz:**
                String classContent = generatePlaceholderClassContent(fullPackageName, className, classType);
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

    private String generatePlaceholderClassContent(String packageName, String className, String classType) {
        StringBuilder classContent = new StringBuilder();

        // Paket adını ekle
        classContent.append("package ").append(packageName).append(";\n\n");

        // İlgili anotasyonları ve importları ekle
        switch (classType) {
            case "Controller":
                classContent.append("import lombok.RequiredArgsConstructor;\n")
                        .append("import org.springframework.web.bind.annotation.RequestMapping;\n")
                        .append("import org.springframework.web.bind.annotation.RestController;\n")
                        .append("import org.springframework.http.ResponseEntity;\n")
                        .append("import org.springframework.web.bind.annotation.GetMapping;\n\n");
                classContent.append("@RestController\n")
                        .append("@RequestMapping(\"/api/").append(className.toLowerCase()).append("\")\n")
                        .append("@RequiredArgsConstructor\n");
                break;

            case "Service":
            case "Business":
                classContent.append("import lombok.RequiredArgsConstructor;\n")
                        .append("import org.springframework.stereotype.Service;\n")
                        .append("import org.springframework.transaction.annotation.Transactional;\n\n");
                classContent.append("@Service\n")
                        .append("@Transactional\n")
                        .append("@RequiredArgsConstructor\n");
                break;

            case "Repository":
                // Repository ve JpaRepository import işlemi
                classContent.append("import org.springframework.data.jpa.repository.JpaRepository;\n")
                        .append("import org.springframework.stereotype.Repository;\n\n")
                        .append("import ").append(packageName).append(".entity.").append(className.replace("Repository", "Entity")).append(";\n\n"); // Entity sınıfını import et
                classContent.append("@Repository\n")
                        .append("public interface ").append(className).append(" extends JpaRepository<")
                        .append(className.replace("Repository", "Entity")).append(", Long> {\n\n}")
                        .append("\n");
                return classContent.toString();


            case "Config":
                classContent.append("import org.springframework.context.annotation.Bean;\n")
                        .append("import org.springframework.context.annotation.Configuration;\n\n");
                classContent.append("@Configuration\n");
                break;

            case "Entity":
                classContent.append("import jakarta.persistence.Entity;\n\n")
                        .append("import lombok.Getter;\n")
                        .append("import lombok.Setter;\n\n");
                classContent.append("@Entity\n")
                        .append("@Getter\n")
                        .append("@Setter\n");
                break;

            case "DTO":
                classContent.append("import lombok.Data;\n\n");
                classContent.append("@Data\n");
                break;

            case "Helper":
                classContent.append("import lombok.extern.slf4j.Slf4j;\n\n");
                classContent.append("@Slf4j\n");
                break;

            case "Impl":
                classContent.append("import lombok.RequiredArgsConstructor;\n")
                        .append("import lombok.extern.slf4j.Slf4j;\n")
                        .append("import org.springframework.stereotype.Service;\n\n");
                classContent.append("@Slf4j\n")
                        .append("@RequiredArgsConstructor\n")
                        .append("@Service\n");
                break;

            case "Exception":
                classContent.append("import org.springframework.http.HttpStatus;\n")
                        .append("import org.springframework.web.bind.annotation.ResponseStatus;\n\n");
                classContent.append("@ResponseStatus(HttpStatus.BAD_REQUEST)\n");
                break;

            case "Util":
                classContent.append("import java.util.Objects;\n\n");
                break;

            default:
                break;
        }

        // Sınıf tanımını oluştur
        classContent.append("public class ").append(className).append(" {\n\n");
        classContent.append("    // Başarılar arkadaşlar\n\n");
        classContent.append("}");

        return classContent.toString();
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SpringBootPackageCreator app = new SpringBootPackageCreator();
            app.setVisible(true);
        });
    }
}
