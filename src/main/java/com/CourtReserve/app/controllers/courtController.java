package com.CourtReserve.app.controllers;

import com.CourtReserve.app.models.Court;
import com.CourtReserve.app.repositories.CourtRepository;
import com.CourtReserve.app.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class courtController {
    @Autowired
    CourtRepository courtRepository;
    private final GameRepository gameRepository;

    public courtController(CourtRepository courtRepository,
                           GameRepository gameRepository) {
        this.courtRepository = courtRepository;
        this.gameRepository = gameRepository;
    }

    @RequestMapping("/courts")
    public String showAddCourt(Model model){
        Iterable<Court> courts = courtRepository.findAll();
        model.addAttribute("courts", courts);
        model.addAttribute("games", gameRepository.findAll());

        return "masters/court";
    }

    @PostMapping("/courts")
    public String addCourt(@ModelAttribute Court court, Model model, HttpServletRequest request){
        String startdate=request.getParameter("fromDate");
        String enddate=request.getParameter("toDate");
        System.out.println(startdate);
        System.out.println(enddate);
        System.out.println(court);
        List<Court> courts = courtRepository.findByName(court.getName().toString());
        String gameCode = court.getName().split("-")[0] ;
        String code =  gameCode+ "-"+ 1;
        if (!courts.isEmpty()){
            Court courtLatest = courts.get(courts.size()-1);
            code =  gameCode+"-" + (Integer.parseInt(courtLatest.getCode().split("-")[1]) + 1) ;
        }
        court.setCode(code);
        court.setName(court.getName().toString().split("-")[1]);
        court.setStartDate(startdate);
        court.setEndDate(enddate);
        courtRepository.save(court);

        return "redirect:/courts";
    }

}
