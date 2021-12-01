package ma.octo.assignement.web;

import ma.octo.assignement.domain.Compte;
import ma.octo.assignement.domain.Utilisateur;
import ma.octo.assignement.domain.Versement;
import ma.octo.assignement.dto.VersementDto;
import ma.octo.assignement.exceptions.*;
import ma.octo.assignement.repository.CompteRepository;
import ma.octo.assignement.repository.UtilisateurRepository;
import ma.octo.assignement.repository.VersementRepository;
import ma.octo.assignement.service.AutiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/versements")
public class VersementController {

    public static final BigDecimal MONTANT_MINIMAL = new BigDecimal("10");

    Logger LOGGER = LoggerFactory.getLogger(VersementController.class);

    @Autowired
    private CompteRepository compteRepository;
    @Autowired
    private VersementRepository versementRepository;
    @Autowired
    private AutiService autiService;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("lister_versement")
    List<Versement> loadAll() {
        List<Versement> all = versementRepository.findAll();

        if (CollectionUtils.isEmpty(all)) {
            return null;
        } else {
            return all;
        }
    }

    @PostMapping("/executerVersements")
    @ResponseStatus(HttpStatus.CREATED)
    public Versement createTransaction(@RequestBody VersementDto versementDto)
            throws TransactionException, RIBNonExistantException, UtilisateurNonExistantException, UnexpectedErrorException {

        Utilisateur utilisateurEmetteur = utilisateurRepository.findByUsername(versementDto.getNomEmetteur());

        Compte comptebeneficiaire = compteRepository.findByRib(versementDto.getNrCompteBeneficiaire());

        if (utilisateurEmetteur == null) {
            throw new UtilisateurNonExistantException("Utilisateur Non existant");
        }

        if (comptebeneficiaire == null) {
            throw new RIBNonExistantException("RIB Non existant");
        }

        if (versementDto.getMotifVersement().trim().equals("") || versementDto.getMotifVersement() == null) {
            throw new TransactionException("Montant Vide");
        }
        else if (versementDto.getMontantVersement().equals(0) || versementDto.getMontantVersement().equals(null)) {
            throw new TransactionException("Montant vide");
        } else if (versementDto.getMontantVersement().compareTo(MONTANT_MINIMAL) < 0) {
            throw new TransactionException("Montant minimal de versement est 10");
        }


        comptebeneficiaire.setSolde(comptebeneficiaire.getSolde().add(versementDto.getMontantVersement()));

        if(compteRepository.save(comptebeneficiaire) == null) {
            throw new UnexpectedErrorException("Probleme se produit");
        }

        Versement versement = new Versement();
        versement.setDateExecution(versementDto.getDate());
        versement.setCompteBeneficiaire(comptebeneficiaire);
        versement.setMontantVersement(versementDto.getMontantVersement());

        if(versementRepository.save(versement) == null) {
            throw new UnexpectedErrorException("Probleme se produit");
        }

        autiService.auditVersement("Versement depuis " + versementDto.getNomEmetteur() + " vers " + versementDto.getNrCompteBeneficiaire()+ " d'un montant de " + versementDto.getMontantVersement()
                .toString());

        return versement;
    }

}
