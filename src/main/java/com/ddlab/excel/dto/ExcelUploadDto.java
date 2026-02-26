package com.ddlab.excel.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExcelUploadDto {

	private String platformName;
	private MultipartFile file;
}
