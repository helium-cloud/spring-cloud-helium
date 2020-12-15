package org.helium.framework.spring.controller;

import org.helium.framework.spring.service.LicService;
import org.helium.framework.spring.service.ResultCode;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/lic")
public class LicenseController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LicenseController.class);


	@Autowired
	private LicService licService;

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public ResultCode addLic(@RequestParam(value = "lic", required = false) String lic){
		LOGGER.info("setLic is [{}]", lic);
		licService.addLicense(lic);
		return ResultCode.OK(licService.getLicense());
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public ResultCode getLic(){
		String lic = licService.getLicense();
		LOGGER.info("getLic is [{}]", lic);
		return ResultCode.OK(lic);
	}
	@RequestMapping(value = "/del", method = RequestMethod.GET)
	public ResultCode delLic(@RequestParam(value = "key", required = false) String key){
		if (!StringUtils.isNullOrEmpty(key) && key.equalsIgnoreCase("DELLIC")){
			LOGGER.info("delLic:[{}]", licService.getLicense());
			licService.deleteLicense();
			return ResultCode.OK(licService.getLicense());
		}
		return ResultCode.ERROR();
	}
}

