package com.project.VehicleInsurancePolicyAndClaim.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.project.VehicleInsurancePolicyAndClaim.model.Customer;
import com.project.VehicleInsurancePolicyAndClaim.model.Vehicle;
import com.project.VehicleInsurancePolicyAndClaim.model.VehicleType;
import com.project.VehicleInsurancePolicyAndClaim.repository.VehicleRepository;
import com.project.VehicleInsurancePolicyAndClaim.service.ClaimService;
import com.project.VehicleInsurancePolicyAndClaim.service.VehicleService;

import jakarta.servlet.http.HttpSession;

@Controller
public class VehicleController {
	@Autowired
	private VehicleService vehicleService;
	
	@Autowired
	private ClaimService claimService;
	
	
	
	@GetMapping("/vehicles")
	public String listVehicles(HttpSession session,Model model) {
		Customer customer = (Customer) session.getAttribute("loggedInCustomer");
		if(customer==null) return "redirect:/login";
		
		model.addAttribute("vehicles",vehicleService.getVehiclesByCustomer(customer));
		return "vehicle/vehicles";
	}
	
	@GetMapping("/addnewvehicle")
	public String showAddForm(Model model) {
		model.addAttribute("vehicle",new Vehicle());
		model.addAttribute("vehicleTypes",VehicleType.values());
		return "vehicle/addVehicle";
	}
	
	@PostMapping("/addnewvehicle")
	public String addVehicle(@ModelAttribute Vehicle vehicle,@RequestParam("image")MultipartFile imageFile,HttpSession session) throws IOException{
		Customer customer = (Customer)session.getAttribute("loggedInCustomer");
		vehicle.setCustomer(customer);
		vehicleService.saveVehicle(vehicle, imageFile);
		return "redirect:/vehicles";
	}
	
	@GetMapping("/vehicle/{id}")
	public String viewVehicle(@PathVariable Long id, Model model) {
		Vehicle vehicle = vehicleService.getVehicleById(id);
		model.addAttribute("vehicle",vehicleService.getVehicleById(id));
		model.addAttribute("claims", claimService.getClaimsByVehicle(vehicle));
		return "vehicle/viewVehicle";
	}
}
