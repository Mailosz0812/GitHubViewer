package org.mailosz.githubviewer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{username}")
    public List<Repository> getUserInfo(@PathVariable(name = "username") String username){
        return accountService.getRepositories(username);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ApiResponseError> handle4xxExceptions(ClientException e){
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ApiResponseError(e.getStatusCode(), e.getMessage()));
    }

}
