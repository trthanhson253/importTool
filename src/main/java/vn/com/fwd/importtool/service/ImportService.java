package vn.com.fwd.importtool.service;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
//	Resource getResourceFileUpload(Long templateId, String fileName) throws Exception;
//	Resource getResourceFileTemplate(Long templateId) throws Exception;
	void importData(Long templateId, MultipartFile file) throws Exception;
//	boolean checkExistFileImport(Long templateId, String fileName) throws Exception;
//	boolean checkExistFileTemplate(Long templateId, String templateName) throws Exception;
//	void storeFileTemplate(Long templateId, MultipartFile file) throws Exception;
	File getFileTemplate(Long templateId) throws Exception;
	File getFileUpload(Long logId) throws Exception;
	List<Object[]> readExcel4LoadData(MultipartFile fileImport) throws Exception;
}
