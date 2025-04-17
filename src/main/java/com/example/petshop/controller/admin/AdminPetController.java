package com.example.petshop.controller.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.petshop.entity.Pet;
import com.example.petshop.service.PetService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/pets")
public class AdminPetController {

    @Autowired
    private PetService petService;

    @GetMapping
    public String listPets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Pet> petPage = petService.findAll(pageable);

        model.addAttribute("pets", petPage.getContent());
        model.addAttribute("currentPage", petPage.getNumber());
        model.addAttribute("totalPages", petPage.getTotalPages());
        model.addAttribute("totalItems", petPage.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/pet/list";
    }

    @GetMapping("/add")
    public String showAddPetForm(Model model) {
        model.addAttribute("pet", new Pet());
        return "admin/pet/add";
    }

    @PostMapping("/add")
    public String addPet(
            @Valid @ModelAttribute("pet") Pet pet,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/pet/add";
        }

        // Thiết lập trạng thái mặc định nếu trống
        if (pet.getStatus() == null || pet.getStatus().isEmpty()) {
            pet.setStatus("AVAILABLE");
        }

        // Lưu thú cưng
        Pet savedPet = petService.save(pet);

        // Upload ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                petService.addPetImage(savedPet.getId(), imageFile, true);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Không thể tải lên ảnh: " + e.getMessage());
            }
        }

        redirectAttributes.addFlashAttribute("success", "Thêm thú cưng thành công!");

        return "redirect:/admin/pets";
    }

    @GetMapping("/edit/{id}")
    public String showEditPetForm(@PathVariable Long id, Model model) {
        Pet pet = petService.findById(id);
        model.addAttribute("pet", pet);
        return "admin/pet/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePet(
            @PathVariable Long id,
            @Valid @ModelAttribute("pet") Pet pet,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/pet/edit";
        }

        // Lưu thú cưng
        petService.update(pet);

        // Upload ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                petService.addPetImage(pet.getId(), imageFile, true);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Không thể tải lên ảnh: " + e.getMessage());
            }
        }

        redirectAttributes.addFlashAttribute("success", "Cập nhật thú cưng thành công!");

        return "redirect:/admin/pets";
    }

    @GetMapping("/delete/{id}")
    public String deletePet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            petService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa thú cưng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa thú cưng: " + e.getMessage());
        }

        return "redirect:/admin/pets";
    }

    @GetMapping("/status/{id}/{status}")
    public String updatePetStatus(
            @PathVariable Long id,
            @PathVariable String status,
            RedirectAttributes redirectAttributes) {

        try {
            petService.updatePetStatus(id, status.toUpperCase());
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thú cưng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật trạng thái: " + e.getMessage());
        }

        return "redirect:/admin/pets";
    }

    @GetMapping("/images/{petId}")
    public String managePetImages(@PathVariable Long petId, Model model) {
        Pet pet = petService.findById(petId);
        model.addAttribute("pet", pet);
        return "admin/pet/images";
    }

    @PostMapping("/images/{petId}/add")
    public String addPetImage(
            @PathVariable Long petId,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary,
            RedirectAttributes redirectAttributes) {

        try {
            petService.addPetImage(petId, imageFile, isPrimary);
            redirectAttributes.addFlashAttribute("success", "Thêm ảnh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể tải lên ảnh: " + e.getMessage());
        }

        return "redirect:/admin/pets/images/" + petId;
    }

    @GetMapping("/images/{petId}/delete/{imageId}")
    public String deletePetImage(
            @PathVariable Long petId,
            @PathVariable Long imageId,
            RedirectAttributes redirectAttributes) {

        try {
            petService.deletePetImage(imageId);
            redirectAttributes.addFlashAttribute("success", "Xóa ảnh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa ảnh: " + e.getMessage());
        }

        return "redirect:/admin/pets/images/" + petId;
    }
}
