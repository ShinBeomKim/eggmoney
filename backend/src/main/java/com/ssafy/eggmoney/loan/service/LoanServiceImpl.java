package com.ssafy.eggmoney.loan.service;

import com.ssafy.eggmoney.account.entity.AccountLogType;
import com.ssafy.eggmoney.account.service.AccountService;
import com.ssafy.eggmoney.common.exception.ErrorType;
import com.ssafy.eggmoney.loan.entity.Loan;
import com.ssafy.eggmoney.loan.repository.LoanLogRepository;
import com.ssafy.eggmoney.loan.repository.LoanRepository;
import com.ssafy.eggmoney.loan.dto.request.LoanCreateRequestDto;
import com.ssafy.eggmoney.loan.dto.request.LoanEvaluationRequestDto;
import com.ssafy.eggmoney.loan.dto.response.LoanDetailResponseDto;
import com.ssafy.eggmoney.loan.dto.response.LoanLogListResponseDto;
import com.ssafy.eggmoney.loan.dto.response.LoanPrivateListResponseDto;
import com.ssafy.eggmoney.loan.entity.LoanLog;
import com.ssafy.eggmoney.loan.entity.LoanStatus;
import com.ssafy.eggmoney.loan.entity.LoanType;
import com.ssafy.eggmoney.loan.repository.LoanLogRepository;
import com.ssafy.eggmoney.loan.repository.LoanRepository;
import com.ssafy.eggmoney.notification.dto.request.NotificationRequest;
import com.ssafy.eggmoney.notification.entity.NotificationType;
import com.ssafy.eggmoney.notification.service.NotificationService;
import com.ssafy.eggmoney.user.entity.User;
import com.ssafy.eggmoney.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final LoanLogRepository loanLogRepository;
    private final AccountService accountService;
    private final NotificationService notificationService;

    // 대출 생성하기
    @Override
    @Transactional
    public void createLoan(LoanCreateRequestDto requestDto, User user) {

        if(!user.getRole().equals("자녀")){
            throw new AccessDeniedException(ErrorType.NOT_CREATED_ROLE.toString());
        }

        Loan loan = Loan.builder()
                .user(user)
                .loanType(requestDto.getLoanType())
                .loanStatus(LoanStatus.PROGRESS)
                .loanAmount(requestDto.getLoanAmount())
                .loanDate(requestDto.getLoanDate())
                .balance(requestDto.getLoanAmount())
                .loanReason(requestDto.getLoanReason())
                .expirationDate(LocalDateTime.now().plusMonths(requestDto.getLoanDate()))
                .build();

        loanRepository.save(loan);

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .notificationType(NotificationType.대출요청)
                .message("자녀가 대출을 요청했습니다.")
                .build();

        notificationService.saveNotification(user.getId(), notificationRequest);
    }

    // 개인 대출 내역 조회하기(부모면 자녀 것 모두, 자녀는 본인 것)
    @Override
    @Transactional(readOnly = true)
    public List<LoanPrivateListResponseDto> getPrivateLoans(User user) {

        List<Long> userIds = new ArrayList<>();
        if(user.getRole().equals("부모")){
            List<User> family = userRepository.findAllByFamilyId(user.getFamily().getId());
            for(User u : family){
                if(u.getRole().equals("자녀")){
                    userIds.add(u.getId());
                }
            }
        }else {
            userIds.add(user.getId());
        }

        // 자녀의 대출 리스트 조회
        List<Loan> loans = loanRepository.findAllByUserIdOrderByCreatedAtDesc(userIds);

        List<LoanPrivateListResponseDto> loanList = loans.stream().map(
                loan -> LoanPrivateListResponseDto.builder()
                        .loanId(loan.getId())
                        .userName(loan.getUser().getName())
                        .loanType(loan.getLoanType())
                        .loanStatus(loan.getLoanStatus())
                        .loanAmount(loan.getLoanAmount())
                        .loanDate(loan.getLoanDate())
                        .balance(loan.getBalance())
                        .loanReason(loan.getLoanReason())
                        .refuseReason(loan.getRefuseReason())
                        .loanRate(loan.getLoanRate())
                        .expirationDate(loan.getExpirationDate())
                        .createdAt(loan.getCreatedAt())
                        .updatedAt(loan.getUpdatedAt())
                        .build()
        ).collect(Collectors.toList());

        return loanList;
    }

    // 상세대출내역 조회(승인 된 대출내역만)
    @Override
    @Transactional(readOnly = true)
    public LoanDetailResponseDto getDetailLoan(long loanId) {

        Loan loan = loanRepository.findByIdAndLoanStatus(loanId, LoanStatus.APPROVAL).orElseThrow(
                () -> new NoSuchElementException(ErrorType.NOT_FOUND_LOAN.toString())
        );

        LoanDetailResponseDto loanDetail = LoanDetailResponseDto.builder()
                .loanId(loanId)
                .createdAt(loan.getCreatedAt())
                .expirationDate(loan.getExpirationDate())
                .loanAmount(loan.getLoanAmount())
                .balance(loan.getBalance())
                .refuseReason(loan.getRefuseReason())
                .loanRate(loan.getLoanRate())
                .loanType(loan.getLoanType())
                .build();

        return loanDetail;
    }

    // 대출 심사하기
    @Override
    @Transactional
    public void loanEvaluation(long loanId, LoanEvaluationRequestDto requestDto, User user) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(
                () -> new NoSuchElementException(ErrorType.NOT_FOUND_LOAN.toString())
        );
        if(!user.getRole().equals("부모")){
            throw new AccessDeniedException(ErrorType.NOT_JUDGE_ROLE.toString());
        }

        Loan updateLoan = loan.toBuilder()
                .loanStatus(requestDto.getLoanStatus())
                .loanRate(requestDto.getLoanRate())
                .refuseReason(requestDto.getRefuseReason())
                .build();

        // 대출 승인시 메인 계좌 반영
        if ( requestDto.getLoanStatus().toString().equals("APPROVAL")) {
            accountService.updateAccount(AccountLogType.LOAN, loan.getUser().getId(), loan.getLoanAmount());
        }

        loanRepository.save(updateLoan);

        NotificationType type = NotificationType.대출거절;
        String notificationMessage = "부모님이 대출을 거절하셨습니다.";
        if(requestDto.getLoanStatus() == LoanStatus.APPROVAL){
            notificationMessage = "부모님이 대출을 승인하셨습니다.";
            type = NotificationType.대출승인;
        }

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .notificationType(type)
                .message(notificationMessage)
                .receiveUserId(updateLoan.getUser().getId())
                .build();

        notificationService.saveNotification(user.getId(), notificationRequest);
    }

    // 대출금 상환하기
    @Override
    @Transactional
    public void sendRepayment(long loanId) {

        Loan loan = loanRepository.findByIdAndLoanStatus(loanId, LoanStatus.APPROVAL).orElseThrow(
                () -> new NoSuchElementException(ErrorType.NOT_FOUND_LOAN.toString())
        );

        int interest = (int) (loan.getBalance() * loan.getLoanRate() / 100 * 1 / 12);
        int repayment = (loan.getLoanType() == LoanType.EQUALR) ? loan.getLoanAmount() / loan.getLoanDate() : 0;

        if(loan.getBalance() == 0){
            repayment = 0;
        }else if(loan.getBalance() < repayment){
            repayment = loan.getBalance();
        }

        accountService.updateAccount(AccountLogType.LOAN, loan.getUser().getId(), -1 * (interest) + repayment);

        Loan updateLoan = loan.toBuilder()
                .balance(loan.getBalance() - repayment) // 상환한 금액 차감
                .build();

        loanRepository.save(updateLoan);

        LoanLog loanLog = LoanLog.builder()
                .loan(updateLoan)
                .balance(updateLoan.getBalance())
                .repayment(repayment + interest)
                .build();

        loanLogRepository.save(loanLog);
    }

    // 대출 로그 조회하기
    @Override
    @Transactional(readOnly = true)
    public List<LoanLogListResponseDto> getLoanLogs(long loanId) {
        List<LoanLog> loanLogs = loanLogRepository.findAllByLoanIdOrderByCreatedAtDesc(loanId);

        List<LoanLogListResponseDto> logList = loanLogs.stream().map(
                (loanLog) -> LoanLogListResponseDto.builder()
                        .id(loanLog.getId())
                        .createdAt(loanLog.getCreatedAt())
                        .repayment(loanLog.getRepayment())
                        .balance(loanLog.getBalance())
                        .build()
                ).collect(Collectors.toList());

        return logList;
    }

    // 대출 만기 상환(남은 금액 메인계좌에서 다 빼기)
    @Override
    @Transactional
    public void expiredRepayment(Long loanId) {

        Loan loan = loanRepository.findByIdAndLoanStatus(loanId, LoanStatus.APPROVAL).orElseThrow(
                ()  -> new NoSuchElementException(ErrorType.NOT_FOUND_LOAN.toString())
        );

        int interest = (int) (loan.getBalance() * loan.getLoanRate() / 100 * 1 / 12);
        accountService.updateAccount(AccountLogType.LOAN, loan.getUser().getId(), -1 * (loan.getBalance() + interest));

        Loan updateLoan = loan.toBuilder()
                .balance(0)
                .loanStatus(LoanStatus.EXPIRED) // 만료 표시
                .build();

        loanRepository.save(updateLoan);

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .receiveUserId(updateLoan.getUser().getId())
                .message("대출이 만기에 도달하여 상환하였습니다.")
                .notificationType(NotificationType.대출상환)
                .build();
        notificationService.saveNotification(null, notificationRequest);
    }

    // 만기 도달한 loanId 찾기(scheduler)
    @Override
    @Transactional(readOnly = true)
    public List<Long> checkingExpired(){
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

        List<Long> loanIds = loanRepository.findIdByLoanStatusAndExpirationDateBetween(LoanStatus.APPROVAL, start, end);

        return loanIds;
    }

}
