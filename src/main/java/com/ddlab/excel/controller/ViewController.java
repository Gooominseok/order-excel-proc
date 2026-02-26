package com.ddlab.excel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ddlab.excel.domain.Platform;
import com.ddlab.excel.repository.PlatformRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ViewController {

	private final PlatformRepository platformRepository;
	
	@GetMapping("/")
	public java.lang.String mainPage(Model model) {
		// DB에 있는 플랫폼 목록(NAVER, COUPANG 등)을 가져와서 화면에 던져줌
		List<Platform> platforms = platformRepository.findAll();
		model.addAttribute("platforms", platforms);
		
		return "main";
	}
	
	@GetMapping("/settings")
    public String settingsPage() {
        return "settings"; // settings.html을 보여줘라!
    }
	
	@GetMapping("/sales")
    public String salesPage() {
        return "sales"; // templates/sales.html 을 찾아가라는 뜻
    }
}
