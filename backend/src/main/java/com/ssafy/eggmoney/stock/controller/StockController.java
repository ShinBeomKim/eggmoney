package com.ssafy.eggmoney.stock.controller;

import com.ssafy.eggmoney.account.dto.responseDto.GetAnalyticsResponseDto;
import com.ssafy.eggmoney.account.service.AccountService;
import com.ssafy.eggmoney.stock.dto.response.StockPriceForYearResponse;
import com.ssafy.eggmoney.stock.dto.response.StockPriceResponse;
import com.ssafy.eggmoney.stock.entity.StockItem;
import com.ssafy.eggmoney.stock.service.StockService;
import com.ssafy.eggmoney.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StockController {
    private final StockService stockService;
    private final AccountService accountService;
    private final UserService userService;

    @GetMapping("/stock/price")
    public ResponseEntity<Map<String, Object>> getLatestStockPrice() {
        Map<String, Object> response = new HashMap<>();

        List<StockPriceResponse> stockPrices = stockService.findLatestStockPrice();
        response.put("stockPrices", stockPrices);

        LocalDate date = stockService.findLatestDate().toLocalDate();
        response.put("date", date);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stock/user/{userId}/available-balance")
    public ResponseEntity<Map<String, Integer>> getInvestableRatio(@PathVariable Long userId) {
        Map<String, Integer> response = new HashMap<>();

        GetAnalyticsResponseDto analytics = accountService.getAnalytics(userId);
        int assets = 0;
        if(analytics.getMainAccountBalance() != null) {
            assets += analytics.getMainAccountBalance();
        }
        if(analytics.getDeposit() != null) {
            assets += analytics.getDeposit();
        }
        if(analytics.getSavings() != null) {
            assets += analytics.getSavings();
        }
        if(analytics.getLoan() != null) {
            assets += analytics.getLoan();
        }
        if(analytics.getStock() != null) {
            assets += analytics.getStock();
        }

        BigDecimal investableRatio = BigDecimal.valueOf(userService.findInvestableRatio(userId));
        int investablePrice = investableRatio
                .divide(BigDecimal.valueOf(100))
                .multiply(BigDecimal.valueOf(assets))
                        .setScale(0, RoundingMode.HALF_UP)
                                .intValue();

        response.put("investablePrice", investablePrice);
        response.put("balance", analytics.getMainAccountBalance());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stock/price/year")
    public ResponseEntity<List<StockPriceForYearResponse>> getStockPricesForYear(@RequestParam StockItem stockItem) {
        return new ResponseEntity<>(stockService.findStockPricesForYear(stockItem), HttpStatus.OK);
    }
}