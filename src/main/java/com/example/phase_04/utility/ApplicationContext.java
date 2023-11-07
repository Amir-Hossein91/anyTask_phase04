package com.example.phase_04.utility;

import com.github.mfathi91.time.PersianDate;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;
//@Component
public class ApplicationContext {
    public static final Path outputPath;
    public static final Path inputPath;
    public static final String sourceAddress;
    public static final String imageName;
    public static final String imageExtension;
    public static final PersianDate currentPersianDate;
    public static final LocalDate currentDate;
    public static final Printer printer;
    public static final Scanner input;

    static{
        sourceAddress = "C:\\Users\\AmirHossein\\IdeaProjects\\anyTask\\image_input";
        imageName = "technician_01";
        imageExtension = "jpg";
        inputPath = Path.of("C:\\Users\\AmirHossein\\IdeaProjects\\anyTask\\image_input\\technician_01.jpg");
        outputPath = Path.of("C:\\Users\\AmirHossein\\IdeaProjects\\anyTask\\image_output\\technician_01.jpg");
        currentPersianDate = PersianDate.now();
        currentDate = currentPersianDate.toGregorian();
        printer = new Printer();
        input = new Scanner(System.in);
    }
}
