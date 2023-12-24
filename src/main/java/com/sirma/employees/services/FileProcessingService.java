package com.sirma.employees.services;

import com.sirma.employees.models.dtos.CommonProjectDTO;
import com.sirma.employees.models.dtos.EmployeePairDTO;
import com.sirma.employees.models.dtos.EmployeePairsWithProjectsDTO;
import com.sirma.employees.models.dtos.EmployeeProjectDTO;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class FileProcessingService {

    private final FileIOService fileService;
    private final List<EmployeeProjectDTO> employeeProjectList;
    private final ResourceLoader resourceLoader;
    private final List<EmployeePairDTO> employeePairs;
    private ArrayList<CommonProjectDTO> commonProjects ;
    private final List<EmployeePairsWithProjectsDTO> employeePairsWithProjects;

    public FileProcessingService(
            FileIOService fileService,
            List<EmployeeProjectDTO> employeeProjectList,
            ResourceLoader resourceLoader,
            ArrayList<CommonProjectDTO> commonProjects,
            List<EmployeePairsWithProjectsDTO> employeePairsWithProjects) {
        this.fileService = fileService;
        this.employeeProjectList = employeeProjectList;
        this.resourceLoader = resourceLoader;
        this.commonProjects = commonProjects;
        this.employeePairsWithProjects = employeePairsWithProjects;
        this.employeePairs = new ArrayList<>();
    }

    public static String transformToUnifiedFormat(String inputDate) {
        try {
            // Attempt to parse the input date using different date formats
            Date parsedDate = parseDate(inputDate);

            // Convert the parsed date to LocalDate
            LocalDate localDate = parsedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            // Format the LocalDate into the unified format (YYYY-MM-DD)
            return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (ParseException e) {
            // Handle parsing exceptions
            e.printStackTrace();
            return null; // or throw an exception based on your needs
        }
    }

    private static Date parseDate(String inputDate) throws ParseException {
        SimpleDateFormat[] dateFormats = {
                new SimpleDateFormat("yyyy-MM-dd"),
                new SimpleDateFormat("MM/dd/yyyy"),
                new SimpleDateFormat("yy/MM/dd"),
                new SimpleDateFormat("dd.MM.yyyy"),
                new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH),
        };

        for (SimpleDateFormat dateFormat : dateFormats) {
            try {
                return dateFormat.parse(inputDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // If none of the formats match, throw an exception
        throw new ParseException("Unsupported date format", 0);
    }

    public EmployeePairsWithProjectsDTO processFile(String fileName) {
        List<String> lines = new ArrayList<>();

        try {
            InputStream inputStream = resourceLoader.getResource("classpath:files/" + fileName).getInputStream();

            Long idCounter = 1L;
            LocalDate startOfOverlappingPeriod = null;
            LocalDate endOfOverlappingPeriod = null;

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                // Check for BOM and skip if present
                reader.mark(1);
                int firstChar = reader.read();
                if (firstChar != 0xFEFF) {
                    // Not a BOM, so reset the reader
                    reader.reset();
                }

                String line;

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                for (int i = 0; i < lines.size(); i++) {
                    String currentLine = lines.get(i);
                    String[] currentLineContent = currentLine.split(",");

                    if (currentLineContent[0].equals("EmpID")) {
                        continue;
                    }

                    long employeeId = Long.parseLong(currentLineContent[0]);
                    long projectId = Long.parseLong(currentLineContent[1]);
                    LocalDate dateFrom = transformAndParseDate(currentLineContent[2]);
                    LocalDate dateTo = transformAndParseDate(currentLineContent[3]);
                    EmployeeProjectDTO baseEmployee = new EmployeeProjectDTO(idCounter, Long.parseLong(String.valueOf(employeeId)), projectId, dateFrom, dateTo);
                    idCounter++;

                    for (int j = i + 1; j < lines.size(); j++) {
                        String nextLine = lines.get(j);
                        String[] nextLineContent = nextLine.split(",");

                        employeeId = Long.parseLong(nextLineContent[0]);

                        // Check if the program is not comparing to identical employees
                        if (!Objects.equals(baseEmployee.employeeId(), employeeId)) {
                            projectId = Long.parseLong(nextLineContent[1]);

                            // Check if the project is the same
                            if (Objects.equals(baseEmployee.projectId(), projectId)) {
                                dateFrom = transformAndParseDate(nextLineContent[2]);
                                dateTo = transformAndParseDate(nextLineContent[3]);
                                EmployeeProjectDTO employeeToCompare = new EmployeeProjectDTO(idCounter, employeeId, projectId, dateFrom, dateTo);
                                idCounter++;

                                boolean areDatesOverlapping = areOverlapping(baseEmployee.dateFrom(), baseEmployee.dateTo(), employeeToCompare.dateFrom(), employeeToCompare.dateTo());

                                // Check if the dates of the different employees are overlapping
                                if (areDatesOverlapping) {
                                    // Check which date comes first to extract and compare later
                                    if (baseEmployee.dateFrom().isBefore(employeeToCompare.dateFrom())) {
                                        startOfOverlappingPeriod = employeeToCompare.dateFrom();
                                    } else {
                                        startOfOverlappingPeriod = baseEmployee.dateFrom();
                                    }

                                    // Check which end date comes first to extract and compare later
                                    if (baseEmployee.dateTo().isBefore(employeeToCompare.dateTo())) {
                                        endOfOverlappingPeriod = baseEmployee.dateTo();
                                    } else {
                                        endOfOverlappingPeriod = employeeToCompare.dateTo();
                                    }
                                }

                                var totalMonths = calculateTotalTimeOnProject(startOfOverlappingPeriod, endOfOverlappingPeriod);

                                EmployeePairDTO employeePair = new EmployeePairDTO(baseEmployee.employeeId(), employeeToCompare.employeeId(), totalMonths);

                                CommonProjectDTO commonProject = new CommonProjectDTO(baseEmployee.projectId(), totalMonths);

                                // Create predicate to check if any of the combinations of the two employees exist already in the collection
                                Predicate<EmployeePairDTO> getExistingEmployeePair = e -> e.employeeOneId().equals(employeePair.employeeOneId()) && e.employeeTwoId().equals(employeePair.employeeTwoId())
                                        || e.employeeOneId().equals(employeePair.employeeTwoId()) && e.employeeTwoId().equals(employeePair.employeeOneId());

                                boolean containsEmployees = employeePairs.stream().anyMatch(getExistingEmployeePair);

                                // Check if the collection contains the employee pair on another project and update their total months together
                                if (containsEmployees) {
                                    var existingEmployeePair = employeePairs.stream().filter(getExistingEmployeePair).findFirst();
                                    existingEmployeePair.ifPresent(employeePairs::remove);
                                    var existingTotalMonths = existingEmployeePair.map(EmployeePairDTO::getTotalMonths).orElse(null);
                                    // Use the getOrDefault method to handle the potential null value
                                    long existingTotalMonthsValue = existingTotalMonths != null ? existingTotalMonths : 0L;

                                    EmployeePairDTO updatedEmployeePair = new EmployeePairDTO(
                                            baseEmployee.employeeId(),
                                            employeeToCompare.employeeId(),
                                            totalMonths + existingTotalMonthsValue);

                                    employeePairs.add(updatedEmployeePair);

                                    ArrayList<CommonProjectDTO> existingCommonProjects = employeePairsWithProjects.stream()
                                            .filter(e -> (e.employeePairDTO().employeeOneId() == baseEmployee.employeeId()
                                                    && e.employeePairDTO().employeeTwoId() == employeeToCompare.employeeId()
                                            || (e.employeePairDTO().employeeTwoId() == baseEmployee.employeeId()
                                                    && e.employeePairDTO().employeeOneId() == employeeToCompare.employeeId())))
                                            .flatMap(e -> e.getCommonProjects().stream())
                                            .collect(Collectors.toCollection(ArrayList::new));;

                                    commonProjects = existingCommonProjects;
                                    commonProjects.add(commonProject);
                                    Comparator<CommonProjectDTO> comparator =
                                            Comparator.<CommonProjectDTO, Long>comparing(dto -> dto.projectId());
                                    commonProjects.sort(comparator);
                                    var employeeWithProjects = new EmployeePairsWithProjectsDTO(updatedEmployeePair, commonProjects);

                                    employeePairsWithProjects.replaceAll(e -> {
                                        if ((e.employeePairDTO().employeeOneId() == baseEmployee.employeeId() &&
                                                e.employeePairDTO().employeeTwoId() == employeeToCompare.employeeId())
                                            || (e.employeePairDTO().employeeTwoId() == baseEmployee.employeeId() &&
                                                e.employeePairDTO().employeeOneId() == employeeToCompare.employeeId())){
                                            return employeeWithProjects;
                                        }
                                        return e;
                                    });
                                } else {
                                    employeePairs.add(employeePair);

                                    commonProjects = new ArrayList<>();
                                    commonProjects.add(commonProject);

                                    var employeeWithProjects = new EmployeePairsWithProjectsDTO(employeePair, commonProjects);

                                    employeePairsWithProjects.add(employeeWithProjects);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort the list based on total months in descending order
        // Define a custom comparator based on totalMonths of employeePairDTO
        Comparator<EmployeePairsWithProjectsDTO> comparator =
                Comparator.<EmployeePairsWithProjectsDTO, Long>comparing(dto -> dto.getEmployeePair().getTotalMonths())
                        .reversed();


        // Sort the list using the custom comparator
        employeePairsWithProjects.sort(comparator);

        return employeePairsWithProjects.get(0);
    }


    private Long calculateTotalTimeOnProject(LocalDate startOfOverlappingPeriod, LocalDate endOfOverlappingPeriod) {
        var daysBetween = ChronoUnit.DAYS.between(startOfOverlappingPeriod, endOfOverlappingPeriod);

        return Math.floorDiv(daysBetween, 30);
    }

    private boolean areOverlapping(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private LocalDate transformAndParseDate(String date) {
        if (Objects.equals(date, "NULL")) {
            return LocalDate.now();
        } else {
            String result = transformToUnifiedFormat(date);

            if (result == null) {
                return null;
            } else {
                return LocalDate.parse(result);
            }
        }
    }
}
