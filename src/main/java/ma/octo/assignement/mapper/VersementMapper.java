package ma.octo.assignement.mapper;

import ma.octo.assignement.domain.Versement;
import ma.octo.assignement.dto.VersementDto;

public class VersementMapper {
	
	private static VersementDto versementDto;
	
	public static VersementDto map(Versement versement) {
		
		versementDto = new VersementDto();
		versementDto.setNrCompteBeneficiaire(versement.getCompteBeneficiaire().getNrCompte());
		versementDto.setNomEmetteur(versement.getNom_prenom_emetteur());
		versementDto.setMotifVersement(versement.getMotifVersement());
		versementDto.setDate(versement.getDateExecution());
		versementDto.setMontantVersement(versement.getMontantVersement());

		return versementDto;
		
	}
}
