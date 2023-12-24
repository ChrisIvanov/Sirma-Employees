package com.sirma.employees.controllers;

import com.sirma.employees.models.dtos.EmployeePairDTO;
import com.sirma.employees.services.FileIOService;
import com.sirma.employees.services.FileProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FileProcessingController {
    private static final String UPLOAD_DIR = ".\\src\\main\\resources\\files";
    private final FileIOService fileService;
    private final FileProcessingService fileProcessing;
    private final List<String> uploadedFiles;
    private final ResourceLoader resourceLoader;

    @Autowired
    public FileProcessingController(FileIOService fileService, FileProcessingService fileProcessing, List<String> uploadedFiles, ResourceLoader resourceLoader) {
        this.fileService = fileService;
        this.fileProcessing = fileProcessing;
        this.uploadedFiles = uploadedFiles;
        this.resourceLoader = resourceLoader;
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/uploadStatus";
        }

        try {
            // Create the uploads directory if it doesn't exist
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            // Get the file bytes and save it to the uploads directory
            byte[] bytes = file.getBytes();
            Path filePath = Paths.get(UPLOAD_DIR, file.getOriginalFilename());
            Files.write(filePath, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "File '" + file.getOriginalFilename() + "' uploaded successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message",
                    "Failed to upload file '" + file.getOriginalFilename() + "'");
        }

        return "redirect:/uploadStatus";
    }

    @GetMapping("/uploadStatus")
    public String showUploadStatus(@RequestParam(name = "message", required = false)String message, Model model) {
        model.addAttribute("message", message);
        return "uploadStatus";
    }

    @GetMapping("/uploadForm")
    public String showUploadForm(Model model) {
        model.addAttribute("uploadedFiles", uploadedFiles);
        return "uploadForm";
    }

    @GetMapping("/selectFileToProcess")
    public String processFile(MultipartFile file, Model model) {
        try {
            // Load files from the resources/files folder
            List<String> uploadedFiles = loadFilesFromResources("files");
            // Add the list of files to the model
            model.addAttribute("message", "Result!!!!!!!");
            model.addAttribute("uploadedFiles", uploadedFiles);
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
        }

        return "selectFileToProcess";
    }

    @PostMapping("/processSelectedFile")
    public String processSelectedFile(@RequestParam("selectedFile") String selectedFile, Model model) {
        EmployeePairDTO topPair = fileProcessing.processFile(selectedFile);

        model.addAttribute("result", topPair.toString());
        model.addAttribute("message", "Selected File: " + selectedFile);

        return "fileProcessingResult";
    }

//    private List<String> loadFiles() {
//        // Load the list of files from the specified folder
//        List<String> fileList = new ArrayList<>();
//        File folder = new File(FILES_FOLDER);
//
//        if (folder.exists() && folder.isDirectory()) {
//            File[] files = folder.listFiles();
//
//            if (files != null) {
//                for (File file : files) {
//                    fileList.add(file.getName());
//                }
//            }
//        }
//
//        return fileList;
//    }

    private List<String> loadFilesFromResources(String folder) throws IOException {
        // Get the Path for the resources/files folder
        Path resourcePath = Path.of(resourceLoader.getResource("classpath:" + folder).getURI());

        // List all files in the folder
        return Files.list(resourcePath)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }
}
