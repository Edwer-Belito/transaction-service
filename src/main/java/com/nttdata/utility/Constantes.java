package com.nttdata.utility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Constantes {

	public static final String DEPOSITO = "DEPOSITO";
	public static final String RETIRO = "RETIRO";
	
	
	
	@AllArgsConstructor
	@NoArgsConstructor
	public enum Products{
		
		CUENTA_AHORRO("001","CUENTA AHORRO"),
		CUENTA_CORRIENTE("002","CUENTA CORRIENTE"),
		CUENTA_PLAZO("003","CUENTA PLAZO"),
		CREDITO_PERSONAL("004","CREDITO PERSONAL"),
		CREDITO_EMPRESARIAL("005","CREDITO EMPRESARIAL"),
		TARJETA_CREDITO("006","TARJETA CREDITO");
		
		public String key;
		public String value;
		
		
		
		
	}
}
