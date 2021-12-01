package ma.octo.assignement.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


@Data
public class VersementDto {

	private String nrCompteBeneficiaire;
	private String nomEmetteur;
	private String motifVersement;
	private Date date;
	private BigDecimal montantVersement;

}
