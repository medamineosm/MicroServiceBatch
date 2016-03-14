package com.example.Controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.example.FileProcess.ExcelToCSV;
import com.example.FileProcess.FileProcess;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by PC on 13/03/2016.
 */
@Controller
public class FileUploadController {

    @RequestMapping(method=RequestMethod.GET, value="/")
    //@ResponseBody
    public String Test(){
        return "index";
    }
    @RequestMapping(method= RequestMethod.GET, value="/upload")
    public String provideUploadInfo(Model model) {
        File rootFolder = new File(Application.ROOT);
        List<String> fileNames = Arrays.stream(rootFolder.listFiles())
                .map(f -> f.getName())
                .collect(Collectors.toList());
        model.addAttribute("files",
                Arrays.stream(rootFolder.listFiles())
                    .sorted(Comparator.comparingLong(f -> -1 * f.lastModified()))
                    .map(f -> f.getName())
                    .collect(Collectors.toList())
        );
        return "uploadForm";
    }

    @RequestMapping(method=RequestMethod.POST, value="/upload")
    public String handleFileUpload(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file,Model model,RedirectAttributes redirectAttributes) {
        if(name.contains("/")) {
            redirectAttributes.addFlashAttribute("message","Folder separators not allowed");
            return "redirect:upload";
        }
        if(name.contains("/")) {
            redirectAttributes.addFlashAttribute("message","Relative pathnames not allowed");
            return "redirect:upload";
        }
        File outputFile = null;
        if(!file.isEmpty()) {
            try {
                //System.out.println("im here "+ Application.ROOT + "/" +file.getOriginalFilename());

                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(
                                new File(Application.ROOT + "/" + name))
                );
                FileCopyUtils.copy(file.getInputStream(), stream);

                stream.close();

                String name_2 = file.getOriginalFilename().split("//.")[0]+".csv";
                File inputFile = new File(Application.ROOT + "/" + name);
                outputFile = new File(Application.ROOT + "/" +name_2);

                if(file.getOriginalFilename().contains(".xlsx") || file.getOriginalFilename().contains(".xlx"))
                {
                    ExcelToCSV.convertToXlsx(inputFile,outputFile);
                    name = name_2;
                }

                try{
                    Set<String> list = FileProcess.ReaderIter(Application.ROOT + "/" + name);
                    redirectAttributes.addFlashAttribute("list",list);
                    redirectAttributes.addFlashAttribute("message","You successfully uploaded " + name + "!");

                    if(inputFile.delete()) {
                        System.out.println(inputFile.getName() + " Input File is deleted!");
                    }
                    if(outputFile.delete()) {
                        System.out.println(outputFile.getName() + " Outpit File is deleted!");
                    }
                }catch (Exception e) {
                    redirectAttributes.addFlashAttribute("message","ISBN not Found !");
                }

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "You failed to ulpoad "+name+" => "+e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("message","You failed to upload " + name + " because the file was empty");
        }

        return "redirect:upload";
    }


    // After
    @RequestMapping(method=RequestMethod.POST, value="/uploadAsync")
    public Callable<String> processUpload( @RequestParam("name") String name,
                                           @RequestParam("file") MultipartFile file,Model model,RedirectAttributes redirectAttributes) {
        File outputFile = null;
        if(!file.isEmpty()) {
            try {
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(
                                new File(Application.ROOT + "/" + name))
                );


                FileCopyUtils.copy(file.getInputStream(), stream);
                stream.close();

                String name_2 = file.getOriginalFilename().split("//.")[0]+".csv";
                File inputFile = new File(Application.ROOT + "/" + name);
                outputFile = new File(Application.ROOT + "/" +name_2);

                if(file.getOriginalFilename().contains(".xlsx") || file.getOriginalFilename().contains(".xlx"))
                {
                    ExcelToCSV.convertToXlsx(inputFile,outputFile);
                    name = name_2;
                }
                try{
                    Set<String> list = FileProcess.ReaderIter(Application.ROOT + "/" + name);
                    redirectAttributes.addFlashAttribute("list",list);
                    redirectAttributes.addFlashAttribute("message","You successfully uploaded " + name + "!");
                    if(inputFile.delete()) {
                        System.out.println(inputFile.getName() + " Input File is deleted!");
                    }
                    if(outputFile.delete()) {
                        System.out.println(outputFile.getName() + " Outpit File is deleted!");
                    }
                }catch (Exception e) {
                    redirectAttributes.addFlashAttribute("message","ISBN not Found !");
                }

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "You failed to ulpoad "+name+" => "+e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("message","You failed to upload " + name + " because the file was empty");
        }
        final String finalName = name;
        return new Callable<String>() {
            public String call() throws Exception {
                if(finalName.contains("/")) {
                    redirectAttributes.addFlashAttribute("message","Folder separators not allowed");
                    return "redirect:upload";
                }
                if(finalName.contains("/")) {
                    redirectAttributes.addFlashAttribute("message","Relative pathnames not allowed");
                    return "redirect:upload";
                }
                return "redirect:upload";
            }
        };
    }

    @RequestMapping(method= RequestMethod.GET, value="/uploadAsync")
    public String provideUploadAsyncInfo(Model model) {
        File rootFolder = new File(Application.ROOT);
        List<String> fileNames = Arrays.stream(rootFolder.listFiles())
                .map(f -> f.getName())
                .collect(Collectors.toList());
        model.addAttribute("files",
                Arrays.stream(rootFolder.listFiles())
                        .sorted(Comparator.comparingLong(f -> -1 * f.lastModified()))
                        .map(f -> f.getName())
                        .collect(Collectors.toList())
        );
        return "uploadForm";
    }
}
