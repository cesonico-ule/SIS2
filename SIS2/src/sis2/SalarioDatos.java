package sistemas2;

/**
 *
 * @author Ares Alfayate Santiago
 * @author Bermejo Fernandez Cesar
 */
public class SalarioDatos {
    
    public double cuotaObreraGeneralTrabajador;
    public double cuotaDesempleoTrabajador;
    public double cuotaFormacionTrabajador;
    public double contingenciasComunesEmpresario;
    public double fogasaEmpresario;
    public double desmpleoEmpresario;
    public double formacionEmpresario;
    public double accidentesTrabajoEmpresario;

    public SalarioDatos(double[] datos){
        cuotaObreraGeneralTrabajador = datos[0];
        cuotaDesempleoTrabajador = datos[1];
        cuotaFormacionTrabajador = datos[2];
        contingenciasComunesEmpresario = datos[3];
        fogasaEmpresario = datos[4];
        desmpleoEmpresario = datos[5];
        formacionEmpresario = datos[6];
        accidentesTrabajoEmpresario = datos[7];
    }
    public double[] datosCuotas(){
        double[] datos = new double [8];
        datos[0] = cuotaObreraGeneralTrabajador;
        datos[1] = cuotaDesempleoTrabajador;
        datos[2] = cuotaFormacionTrabajador;
        datos[3] = contingenciasComunesEmpresario;
        datos[4] = fogasaEmpresario;
        datos[5] = desmpleoEmpresario;
        datos[6] = formacionEmpresario;
        datos[7] = accidentesTrabajoEmpresario;
        return datos;
    }

    public double getCuotaObreraGeneralTrabajador() {
        return cuotaObreraGeneralTrabajador;
    }

    public double getCuotaDesempleoTrabajador() {
        return cuotaDesempleoTrabajador;
    }

    public double getCuotaFormacionTrabajador() {
        return cuotaFormacionTrabajador;
    }

    public double getContingenciasComunesEmpresario() {
        return contingenciasComunesEmpresario;
    }

    public double getFogasaEmpresario() {
        return fogasaEmpresario;
    }

    public double getDesmpleoEmpresario() {
        return desmpleoEmpresario;
    }

    public double getFormacionEmpresario() {
        return formacionEmpresario;
    }

    public double getAccidentesTrabajoEmpresario() {
        return accidentesTrabajoEmpresario;
    }

    public void setCuotaObreraGeneralTrabajador(double cuotaObreraGeneralTrabajador) {
        this.cuotaObreraGeneralTrabajador = cuotaObreraGeneralTrabajador;
    }

    public void setCuotaDesempleoTrabajador(double cuotaDesempleoTrabajador) {
        this.cuotaDesempleoTrabajador = cuotaDesempleoTrabajador;
    }

    public void setCuotaFormacionTrabajador(double cuotaFormacionTrabajador) {
        this.cuotaFormacionTrabajador = cuotaFormacionTrabajador;
    }

    public void setContingenciasComunesEmpresario(double contingenciasComunesEmpresario) {
        this.contingenciasComunesEmpresario = contingenciasComunesEmpresario;
    }

    public void setFogasaEmpresario(double fogasaEmpresario) {
        this.fogasaEmpresario = fogasaEmpresario;
    }

    public void setDesmpleoEmpresario(double desmpleoEmpresario) {
        this.desmpleoEmpresario = desmpleoEmpresario;
    }

    public void setFormacionEmpresario(double formacionEmpresario) {
        this.formacionEmpresario = formacionEmpresario;
    }

    public void setAccidentesTrabajoEmpresario(double accidentesTrabajoEmpresario) {
        this.accidentesTrabajoEmpresario = accidentesTrabajoEmpresario;
    }
    
}
