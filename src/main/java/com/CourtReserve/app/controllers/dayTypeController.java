package com.CourtReserve.app.controllers;

import com.CourtReserve.app.models.Court;
import com.CourtReserve.app.models.DayType;
import com.CourtReserve.app.models.SpecialDates;
import com.CourtReserve.app.repositories.DayTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import csv.DownloadCsvReport6;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class dayTypeController {
    @Autowired
    DayTypeRepository dayTypeRepository;
    @Autowired
    Jackson2ObjectMapperBuilder mapperBuilder;
    @RequestMapping("/dayTypes")
    public String showDayTypes(Model model){
        Iterable<DayType> dayTypes = dayTypeRepository.findAll();
        System.out.println(dayTypes);
        model.addAttribute("dayTypes", dayTypes);

        return "masters/dayTypes";
    }
    @PostMapping("/dayTypes")
    public String addDayType(@ModelAttribute DayType dayType, Model model){

        dayTypeRepository.save(dayType);

        return "redirect:/dayTypes";
    }
    @GetMapping("/dayTypeData")
    public String slotViewOrderForm1(Model model, HttpSession session) {
        if (session.getAttribute("loggedIn").equals("true")) {
            // List<User> users = (List<User>) userRepository.findAll();
            List<DayType> dayTypes = dayTypeRepository.findByOrderByIdDesc();
            System.out.println("dayTypes:" + dayTypes);
            model.addAttribute("DayType", dayTypes);
            return "admin/dayTypeData";
        }
        List messages = new ArrayList<>();
        messages.add("Login First");
        model.addAttribute("messages", messages);
        return "redirect:/loginPage";
    }
    @PostMapping("/dayTypeData")
    public @ResponseBody String slotViewOrder1(Model model, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException {
        System.out.println("88888888888");
        String code = request.getParameter("code");
        System.out.println(code);
        String name = request.getParameter("name");
        System.out.println(name);
        List<DayType> list = new ArrayList<>();
        if(code.equals("all")&&(name.equals("all"))){
            list = (List<DayType>) dayTypeRepository.findByOrderByCodeAsc();
            System.out.println("kalyan:"+list);
        }


        ObjectMapper mapper = mapperBuilder.build();
        String output = mapper.writeValueAsString(list);
        //String output1 = mapper.writeValueAsString(list);
        System.out.println("Excel Size -- " + list.size());

        return output;
    }
    @Autowired
    SpringTemplateEngine springTemplateEngine;
    @GetMapping("/dayTypePdfData")
    public ResponseEntity slotViewPdfUserOrder1(Model model, HttpServletResponse response, HttpServletRequest request) {
        System.out.println("88888888888");
        String code = request.getParameter("code");
        System.out.println(code);
        String name = request.getParameter("name");
        System.out.println(name);
        List<DayType> list = new ArrayList<>();
        if(code.equals("all")&&(name.equals("all"))){
            list = (List<DayType>) dayTypeRepository.findByOrderByCodeAsc();
            System.out.println("kalyan:"+list);
        }

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("list", list);
        String finalhtml = springTemplateEngine.process("admin/dayTypePdfData",context);
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        System.out.println(finalhtml);
        renderer.setDocumentFromString(finalhtml);
        renderer.layout();
        renderer.createPDF(ops, false);
        renderer.finishPDF();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+ request.getSession().getAttribute("loggedMobile").toString()+".pdf").contentType(MediaType.APPLICATION_OCTET_STREAM).body(ops.toByteArray());

    }
    @GetMapping("/dayTypeExcelData")
    public ResponseEntity slotViewPdfUserOrder2(Model model, HttpServletResponse response, HttpServletRequest request) {
        System.out.println("88888888888");
        String code = request.getParameter("code");
        System.out.println(code);
        String name = request.getParameter("name");
        System.out.println(name);
        List<DayType> list1 = new ArrayList<>();
        if(code.equals("all")&&(name.equals("all"))){
            list1 = (List<DayType>) dayTypeRepository.findByOrderByCodeAsc();
            System.out.println("kalyan:"+list1);
        }


        System.out.println("list:"+list1);
        System.out.println("Excel Size -- " + list1.size());
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(request.getSession().getAttribute("loggedMobile").toString());
        HSSFRow Header = sheet.createRow(0);
        int headercellStart = 0;
        String header[] ={"code","name"};
        // String header[]={"gameDate","slotCode","game"};
        DownloadCsvReport6.getCsvReportDownload(response, header, list1, "code.csv");

        return (ResponseEntity) ResponseEntity.status(203);

    }
}
