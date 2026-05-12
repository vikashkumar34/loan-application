package com.loanapp.service;

import com.loanapp.dto.AdminActionLog;
import com.loanapp.entity.LoanApplication;
import com.loanapp.entity.Disbursement;
import com.loanapp.repository.LoanApplicationRepository;
import com.loanapp.repository.DisbursementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private DisbursementRepository disbursementRepository;

    public List<AdminActionLog> getAdminHistory() {
        List<AdminActionLog> history = new ArrayList<>();

        // Get approved applications
        List<LoanApplication> approvedApps = loanApplicationRepository.findByStatus(com.loanapp.entity.LoanStatus.APPROVED);
        history.addAll(approvedApps.stream()
                .map(app -> new AdminActionLog(app.getId(), app.getUser().getFullName(), "admin", "APPROVED", app.getApprovedDate()))
                .collect(Collectors.toList()));

        // Get disbursed applications
        List<Disbursement> disbursements = disbursementRepository.findAll();
        history.addAll(disbursements.stream()
                .map(dis -> new AdminActionLog(dis.getLoanApplication().getId(), dis.getLoanApplication().getUser().getFullName(), dis.getDisbursedByAdmin(), "DISBURSED", dis.getDisbursedDate()))
                .collect(Collectors.toList()));

        // Sort by timestamp descending
        history.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        return history;
    }
}
