package ma.octo.assignement.web;

import ma.octo.assignement.domain.Compte;
import ma.octo.assignement.domain.Utilisateur;
import ma.octo.assignement.domain.Virement;
import ma.octo.assignement.dto.VirementDto;
import ma.octo.assignement.exceptions.CompteNonExistantException;
import ma.octo.assignement.exceptions.SoldeDisponibleInsuffisantException;
import ma.octo.assignement.exceptions.TransactionException;
import ma.octo.assignement.exceptions.UnexpectedErrorException;
import ma.octo.assignement.repository.CompteRepository;
import ma.octo.assignement.repository.UtilisateurRepository;
import ma.octo.assignement.repository.VirementRepository;
import ma.octo.assignement.service.AutiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/virements")

class VirementController {

    public static final BigDecimal MONTANT_MAXIMAL = new BigDecimal("10000");

    public static final BigDecimal MONTANT_MINIMAL = new BigDecimal("10");

    Logger LOGGER = LoggerFactory.getLogger(VirementController.class);

    @Autowired
    private CompteRepository compteRepository;
    @Autowired
    private VirementRepository virementRepository;
    @Autowired
    private AutiService autiService;
    @Autowired
    private UtilisateurRepository utilisateurRepository;


    @PostMapping("/executerVirements")
    @ResponseStatus(HttpStatus.CREATED)
    public Virement createTransaction(@RequestBody VirementDto virementDto)
            throws SoldeDisponibleInsuffisantException, CompteNonExistantException, TransactionException, UnexpectedErrorException {

        Compte compteEmetteur = compteRepository.findByNrCompte(virementDto.getNrCompteEmetteur());

        Compte compteBeneficiaire = compteRepository.findByNrCompte(virementDto.getNrCompteBeneficiaire());

        if (compteEmetteur == null) {
            throw new CompteNonExistantException("Compte Non existant");
        }

        if (compteBeneficiaire == null) {
            throw new CompteNonExistantException("Compte Non existant");
        }

        if (virementDto.getMontantVirement().equals(null) || virementDto.getMontantVirement().equals(0)) {
            throw new TransactionException("Montant vide");
        }else if (virementDto.getMontantVirement().compareTo(MONTANT_MINIMAL) < 0) {
            throw new TransactionException("Montant minimal de virement est 10");
        } else if (virementDto.getMontantVirement().compareTo(MONTANT_MAXIMAL) > 0) {
            throw new TransactionException("Montant maximal de virement dépassé");
        }

        if (virementDto.getMotif().trim().equals("") || virementDto.getMotif() == null ) {
            throw new TransactionException("Motif vide");
        }

        if (compteEmetteur.getSolde().compareTo(virementDto.getMontantVirement()) < 0) {
            LOGGER.error("Solde insuffisant pour l'utilisateur");
            throw new SoldeDisponibleInsuffisantException("Solde insuffisant pour l'utilisateur");
        }

        compteEmetteur.setSolde(compteEmetteur.getSolde().subtract(virementDto.getMontantVirement()));

        if(compteRepository.save(compteEmetteur) == null) {
            throw new UnexpectedErrorException("Probleme se produit");
        }

        compteBeneficiaire.setSolde(compteBeneficiaire.getSolde().add(virementDto.getMontantVirement()));

        if(compteRepository.save(compteBeneficiaire) == null) {
            throw new UnexpectedErrorException("Probleme se produit");
        }

        Virement virement = new Virement();
        virement.setDateExecution(virementDto.getDate());
        virement.setCompteBeneficiaire(compteBeneficiaire);
        virement.setCompteEmetteur(compteEmetteur);
        virement.setMontantVirement(virementDto.getMontantVirement());

        virementRepository.save(virement);

        autiService.auditVirement("Virement depuis " + virementDto.getNrCompteEmetteur() + " vers " + virementDto
                        .getNrCompteBeneficiaire() + " d'un montant de " + virementDto.getMontantVirement()
                        .toString());

        return virement;
    }

}
