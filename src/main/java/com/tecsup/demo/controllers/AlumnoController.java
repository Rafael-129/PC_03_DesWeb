package com.tecsup.demo.controllers;

import com.tecsup.demo.domain.entities.Alumno;
import com.tecsup.demo.services.AlumnoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import java.util.Map;

@Controller
@SessionAttributes("alumno")
public class AlumnoController {

    @Autowired
    AlumnoService alumnoService;

    @RequestMapping(value = "/alumnos", method = RequestMethod.GET)
    public String listar(Model model) {
        model.addAttribute("alumnos", alumnoService.listar());
        return "alumno-listar";
    }

    @RequestMapping(value = "/alumno/form")
    public String crear(Map<String, Object> model) {
        Alumno alumno = new Alumno();
        model.put("alumno", alumno);
        return "alumno-form";
    }

    @RequestMapping(value = "/alumno/form", method = RequestMethod.POST)
    public String guardar(@Valid Alumno alumno, BindingResult result, Model model, SessionStatus status) {
        if(result.hasErrors()) {
            return "alumno-form";
        }
        alumnoService.grabar(alumno);
        status.setComplete();
        return "redirect:/alumnos";
    }

    @RequestMapping(value="/alumno/form/{id}")
    public String editar(@PathVariable(value="id") Integer id, Map<String, Object> model) {
        Alumno alumno = null;
        if(id > 0) {
            alumno = alumnoService.buscar(id);
        } else {
            return "redirect:/alumnos";
        }
        model.put("alumno", alumno);
        return "alumno-form";
    }

    @RequestMapping(value="/alumno/eliminar/{id}")
    public String eliminar(@PathVariable(value="id") Integer id) {
        if(id > 0) {
            alumnoService.eliminar(id);
        }
        return "redirect:/alumnos";
    }
}
