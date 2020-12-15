package org.helium.framework.spring.service;

import org.helium.framework.spring.autoconfigure.LicenseConfig;
import org.helium.framework.spring.utils.LicFile;
import org.helium.framework.spring.utils.LicenseGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LicServiceImpl implements LicService {

	private ScheduledExecutorService aliveService = Executors.newSingleThreadScheduledExecutor();

	private static final Logger LOGGER = LoggerFactory.getLogger(LicServiceImpl.class);

	private static volatile long starter = 0;

	@Autowired
	private LicenseConfig licenseConfig;

	@Override
	public void addLicense(String lic) {
		LicFile.write(lic);
	}

	@Override
	public String getLicense() {
		return LicFile.read();
	}

	@Override
	public boolean checkLicense() {

		String lic = LicFile.read().trim();
		String licCount = LicenseGen.getLicense(licenseConfig.getKey());
		if (lic.equalsIgnoreCase(licCount)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteLicense() {
		LicFile.delete();
		return false;
	}


	@PostConstruct
	public void start() {
		//定时监听
		aliveService.scheduleAtFixedRate(aliveRunAble, 1, 10, TimeUnit.SECONDS);
		starter = System.currentTimeMillis() + licenseConfig.getExpire();
	}

	/**
	 * License检测过期中止
	 */
	Runnable aliveRunAble = new Runnable() {
		@Override
		public void run() {
			try {
				if (!checkLicense()){
					LOGGER.warn("license not install at:{} will exit", new Date(starter));
				}
				if (!checkLicense() && (System.currentTimeMillis() > starter)){
					LOGGER.error("license not install and exit");
					System.out.println("license not install and exit");
					System.exit(0);
				}
			} catch (Exception e) {
				LOGGER.error("aliveRunAble Exception", e);
			}

		}
	};
}
