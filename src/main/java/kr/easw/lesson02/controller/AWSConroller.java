package kr.easw.lesson02.controller;

import kr.easw.lesson02.model.dto.AWSKeyDto;
import kr.easw.lesson02.service.AWSService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import java.util.List;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rest/aws")
public class AWSConroller {
    private final AWSService awsController;

    @PostMapping("/auth")
    private ModelAndView onAuth(AWSKeyDto awsKey) {
        try {
            awsController.initAWSAPI(awsKey);
            return new ModelAndView("redirect:/");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ModelAndView("redirect:/server-error?errorStatus=" + ex.getMessage());
        }
    }

    @GetMapping("/list")
    private List<String> onFileList() {
        return awsController.getFileList();
    }

    @PostMapping("/upload")
    private ModelAndView onUpload(@RequestParam MultipartFile file) {
        try {
            awsController.upload(file);
            return new ModelAndView("redirect:/?success=true");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ModelAndView("redirect:/server-error?errorStatus=" + ex.getMessage());
        }
    }


    @PostMapping("/download")
    private ModelAndView onDownload(@RequestParam String fileName) {
        try {
            // 이곳에 파일 다운로드 로직, 혹은 서비스를 통한 다운로드 호출을 구현하십시오.
            throw new IllegalStateException("기능이 구현되지 않았습니다.");
        } catch (Throwable e) {
            return new ModelAndView("redirect:/server-error?errorStatus=" + e.getMessage());
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            // 파일 다운로드 서비스 호출 및 파일 내용을 가져옴
            byte[] fileContent = awsController.download(fileName);

            // 파일 다운로드를 위한 HTTP 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);

            // 파일 내용을 ResponseEntity에 추가하여 클라이언트에게 전송
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}