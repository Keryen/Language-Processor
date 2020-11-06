package codigoObjeto;

public class Operando {

	private tipoOperando codigo;
	private String operando; // valor como tal, el lugar vaia o valor
	
	public Operando(tipoOperando codigo, String operando) {
		this.codigo = codigo;
		this.operando = operando;
	}
	
	public tipoOperando getCodigo() {
		return codigo;
	}
	
	public void setCodigo(tipoOperando codigo) {
		this.codigo = codigo;
	}
	
	public String getOperando() {
		return operando;
	}
	
	public void setOperando(String operando) {
		this.operando = operando;
	}
}
