package com.tecsup.demo.services;

import com.tecsup.demo.domain.entities.Alumno;
import com.tecsup.demo.domain.persistence.AlumnoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AlumnoServiceImpl implements AlumnoService {
    @Autowired
    private AlumnoDao dao;

    @Override
    @Transactional
    public void grabar(Alumno alumno) {
        dao.save(alumno);
    }

    @Override
    @Transactional
    public void eliminar(int id) {
        dao.deleteById(id);
    }

    @Override
    @Transactional
    public Alumno buscar(Integer id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<Alumno> listar() {
        return (List<Alumno>) dao.findAll();
    }
}
