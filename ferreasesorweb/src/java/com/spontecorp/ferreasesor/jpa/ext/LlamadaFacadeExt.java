package com.spontecorp.ferreasesor.jpa.ext;

import com.spontecorp.ferreasesor.controller.reporte.ReporteHelper;
import com.spontecorp.ferreasesor.jpa.LlamadaFacade;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jgcastillo
 */
public class LlamadaFacadeExt extends LlamadaFacade {
    //@PersistenceContext(unitName = "FerreAsesorWebPU")

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("FerreAsesorWebPU");
    private EntityManager em = emf.createEntityManager();
    private static final Logger logger = LoggerFactory.getLogger(LlamadaFacadeExt.class);

    public List<Object[]> findLlamadas(Date fechaInicio, Date fechaFin) {
        String q = "SELECT ll.fechaClose, count(ll) FROM Llamada ll "
                + "WHERE ll.accion = '0' AND ll.fechaClose >= :fechaInicio AND ll.fechaClose <= :fechaFin "
                + "GROUP BY ll.fechaClose";
        Query query = em.createQuery(q);
        query.setParameter("fechaInicio", fechaInicio);
        query.setParameter("fechaFin", fechaFin);
        return query.getResultList();
    }

    /**
     * Devuelve una lista de llamadas entre dos fechas, dependiendo del query que reciba
     * @param tipo
     * @param fechaInicio
     * @param fechaFin
     * @return 
     */
    public List<Object[]> findLlamadas(int tipo, Date fechaInicio, Date fechaFin) {
        String query = "";
        switch (tipo) {
            case ReporteHelper.LLAMADAS_DISPOSITIVO:
                query = "SELECT b, COUNT(ll) "
                        + "FROM Llamada ll, Distribucion d, Boton b "
                        + "WHERE ll.distribucionId.id = d.id AND d.botonId = b.id "
                        + "AND ll.accion = '0' AND ll.fechaClose >= :fechaInicio AND ll.fechaClose <= :fechaFin "
                        + "GROUP BY b.id";
                break;
            case ReporteHelper.LLAMADAS_ASESOR:
                query = "SELECT a, COUNT(ll) "
                        + "FROM Llamada ll, Distribucion d, Asesor a "
                        + "WHERE ll.distribucionId.id = d.id AND d.asesorId = a.id "
                        + "AND ll.accion = '0' AND ll.fechaClose >= :fechaInicio AND ll.fechaClose <= :fechaFin "
                        + "GROUP BY a.id";
                break;
            case ReporteHelper.LLAMADAS_TURNO:
                query = "SELECT t, COUNT(ll) "
                        + "FROM Llamada ll, Distribucion d, Turno t "
                        + "WHERE ll.distribucionId.id = d.id AND d.turnoId = t.id "
                        + "AND ll.accion = '0' AND ll.fechaClose >= :fechaInicio AND ll.fechaClose <= :fechaFin "
                        + "GROUP BY d.turnoId";
                break;

        }

        List<Object[]> result = null;
        try {
            Query q = em.createQuery(query);
            q.setParameter("fechaInicio", fechaInicio);
            q.setParameter("fechaFin", fechaFin);
            result = q.getResultList();
        } catch (Exception e) {
            logger.error("Error generando los datos: " + e);
        } 
        return result;
    }
    
    /**
     * Cuenta las llamdas entre dos fechas
     * @param fechaInicial
     * @param fechaFin
     * @return la cantidad de llamadas
     */
    public Long getLlamadaCount(Date fechaInicio, Date fechaFin) {
            String query = "SELECT COUNT(ll) FROM Llamada ll "
                    + "WHERE ll.accion = '0' AND ll.fechaClose >= :fechaInicio AND ll.fechaClose <= :fechaFin ";
            Query q = em.createQuery(query);
            q.setParameter("fechaInicio", fechaInicio);
            q.setParameter("fechaFin", fechaFin);
            return (Long)q.getSingleResult();
    }

    /**
     * Cuenta la cantidad de llamdas cerradas entre dos fechas
     * @param fechaInicial
     * @param fechaFin
     * @return 
     */
    public Long getDiasEntreFechasCount(Date fechaInicial, Date fechaFin) {
            String query = "SELECT COUNT(DISTINCT ll.fechaClose) FROM Llamada ll "
                    + "WHERE ll.accion = '0' AND ll.fechaClose >= :fechaInicio AND ll.fechaClose <= :fechaFin ";
            Query q = em.createQuery(query);
            q.setParameter("fechaInicio", fechaInicial);
            q.setParameter("fechaFin", fechaFin);
            return (Long)q.getSingleResult();
    }
}