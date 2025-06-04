package com.tecsup.demo.aop;

import com.tecsup.demo.domain.entities.Auditoria;
import com.tecsup.demo.domain.entities.Curso;
import com.tecsup.demo.domain.persistence.AuditoriaDao;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Calendar;

@Component
@Aspect
public class LogginAspecto {

    private Long tx;

    @Autowired
    private AuditoriaDao auditoriaDao;

    @Around("execution(* com.tecsup.demo.services.*ServiceImpl.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Long currTime = System.currentTimeMillis();
        tx = System.currentTimeMillis();
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String metodo = "[" + tx + "] - " + joinPoint.getSignature().getName();

        if (joinPoint.getArgs().length > 0) {
            logger.info(metodo + " () INPUT: " + Arrays.toString(joinPoint.getArgs()));
        }

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }

        logger.info(metodo + " (): tiempo transcurrido " + (System.currentTimeMillis() - currTime) + " ms.");
        return result;
    }

    @After("execution(* com.tecsup.demo.controllers.*Controller.guardar*(..)) || " +
            "execution(* com.tecsup.demo.controllers.*Controller.editar*(..)) || " +
            "execution(* com.tecsup.demo.controllers.*Controller.eliminar*(..))")
    public void auditoria(JoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String metodo = joinPoint.getSignature().getName();

        Integer id = null;
        if(metodo.startsWith("guardar")){
            Object[] parametros = joinPoint.getArgs();
            Object entidad = parametros[0];
            if (entidad instanceof Curso curso) {
                id = curso.getId();
            } else if (entidad instanceof com.tecsup.demo.domain.entities.Alumno alumno) {
                id = alumno.getId();
            }
        }
        else if(metodo.startsWith("editar")){
            Object[] parametros = joinPoint.getArgs();
            id = (Integer)parametros[0];
        }
        else if(metodo.startsWith("eliminar")){
            Object[] parametros = joinPoint.getArgs();
            id = (Integer)parametros[0];
        }

        String tabla = "cursos";
        if (metodo.startsWith("guardar") && joinPoint.getArgs().length > 0) {
            Object entidad = joinPoint.getArgs()[0];
            if (entidad instanceof com.tecsup.demo.domain.entities.Alumno) {
                tabla = "alumnos";
            }
        }

        String traza = "tx[" + tx + "] - " + metodo;
        logger.info(traza + " (): registrando auditoria...");
        auditoriaDao.save(new Auditoria(tabla, id, Calendar.getInstance().getTime(),
                "usuario", metodo));
    }
}
