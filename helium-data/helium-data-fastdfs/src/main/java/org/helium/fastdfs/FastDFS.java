package org.helium.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.FileInfo;
import org.helium.fastdfs.spi.FastDFSFieldLoader;
import org.helium.framework.annotations.FieldLoaderType;

import java.io.IOException;
import java.util.Map;

/**
 * Created by wuzhiguo on 15-10-13.
 */
@FieldLoaderType(loaderType = FastDFSFieldLoader.class)
public interface FastDFS {
	/**
	 * 上传文件到FastDFS
	 *
	 * @param fileBuffer  文件内容字节数组
	 * @param fileExtName 文件扩展名
	 * @param metaMap     文件元数据
	 * @param storyPath   文件存储路径索引(>=0:有效 <0:使用默认)
	 * @return 成功返回文件ID，失败返回null
	 * @throws Exception
	 */
	String uploadFile(byte[] fileBuffer, String fileExtName, Map<String, String> metaMap, byte storyPath) throws Exception;
	/**
	 * 上传文件到FastDFS
	 *
	 * @param fileBuffer  文件内容字节数组
	 * @param fileExtName 文件扩展名
	 * @param metaMap     文件元数据
	 * @return 成功返回文件ID，失败返回null
	 * @throws Exception
	 */
	String[] uploadFile(byte[] fileBuffer, String fileExtName, Map<String, String> metaMap) throws Exception;
	/**
	 * 上传文件到FastDFS
	 * @param master_file_id  masterID
	 * @param prefix_name  文件名前缀
	 * @param file_buff  文件内容字节数组
	 * @param file_ext_name 文件扩展名
	 * @param metaMap     文件元数据
	 * @return 成功返回文件ID，失败返回null
	 * @throws Exception
	 */
	String upload_file1(String master_file_id, String prefix_name, byte[] file_buff, String file_ext_name, Map<String, String> metaMap) throws IOException, MyException;

	/**
	 * 上传可修改文件到FastDFS
	 *
	 * @param fileBuffer  文件内容字节数组
	 * @param fileExtName 文件扩展名
	 * @param metaMap     文件元数据
	 * @param storyPath   文件存储路径索引(>=0:有效 <0:使用默认)
	 * @return 成功返回可修改文件ID，失败返回null
	 * @throws Exception
	 */
	String uploadAppenderFile(byte[] fileBuffer, String fileExtName, Map<String, String> metaMap, byte storyPath) throws Exception;

	/**
	 * 文件内容追加
	 *
	 * @param appenderFileId 可修改文件ID
	 * @param fileBuffer     追加内容字节数组
	 * @return 成功返回0，失败返回非0
	 * @throws Exception
	 */
	int appendFile(String appenderFileId, byte[] fileBuffer) throws Exception;

	/**
	 * 文件内容追加
	 *
	 * @param appenderFileId 可修改文件ID
	 * @param fileBuffer     追加内容字节数组
	 * @param offset         追加内容的偏移
	 * @param length         追加内容的长度
	 * @return 成功返回0，失败返回非0
	 * @throws Exception
	 */
	int appendFile(String appenderFileId, byte[] fileBuffer, int offset, int length) throws Exception;

	/**
	 * 文件内容修改
	 *
	 * @param appenderFileId 可修改文件ID
	 * @param fileOffset     文件修改位置
	 * @param fileBuffer     修改内容字节数组
	 * @return 成功返回0，失败返回非0
	 * @throws Exception
	 */
	int modifyFile(String appenderFileId, long fileOffset, byte[] fileBuffer) throws Exception;

	/**
	 * 文件内容修改
	 *
	 * @param appenderFileId 可修改文件ID
	 * @param fileOffset     文件修改位置
	 * @param fileBuffer     修改内容字节数组
	 * @param bufferOffset   修改内容的偏移
	 * @param bufferLength   修改内容的长度
	 * @return 成功返回0，失败返回非0
	 * @throws Exception
	 */
	int modifyFile(String appenderFileId, long fileOffset, byte[] fileBuffer, int bufferOffset, int bufferLength) throws Exception;

	/**
	 * 文件内容截断
	 *
	 * @param appenderFileId    可修改文件ID
	 * @param truncatedFileSize 文件内容截断后尺寸
	 * @return 成功返回0，失败返回非0
	 * @throws Exception
	 */
	int truncateFile(String appenderFileId, long truncatedFileSize) throws Exception;

	/**
	 * 删除文件
	 *
	 * @param fileId 文件ID
	 * @return 成功返回0，失败返回非0
	 * @throws Exception
	 */
	int deleteFile(String fileId) throws Exception;

	/**
	 * 下载文件内容
	 *
	 * @param fileId 文件ID
	 * @return 成功返回下载内容，失败返回null
	 * @throws Exception
	 */
	byte[] downloadFile(String fileId) throws Exception;

	/**
	 * 下载文件内容
	 *
	 * @param fileId        文件ID
	 * @param fileOffset    下载文件内容位置
	 * @param downloadBytes 下载文件内容长度，0表示下载从指定位置到文件末尾的全部内容
	 * @return 成功返回下载内容，失败返回null
	 * @throws Exception
	 */
	byte[] downloadFile(String fileId, long fileOffset, long downloadBytes) throws Exception;

	/**
	 * 查询文件信息
	 *
	 * @param fileId 文件ID
	 * @return 成功返回FileInfo对象，失败返回null
	 * @throws Exception
	 */
	FileInfo queryFileInfo(String fileId) throws Exception;

	/**
	 * 获取文件元数据
	 *
	 * @param fileId 文件ID
	 * @return 成功返回元数据Map，失败返回null
	 * @throws Exception
	 */
	Map<String, String> getMetadata(String fileId) throws Exception;

	/**
	 * 设置文件元数据，存在覆盖，不存在插入
	 *
	 * @param fileId  文件ID
	 * @param metaMap 文件元数据
	 * @return 成功返回0，失败返回非0
	 * @throws Exception
	 */
	int setMetadata(String fileId, Map<String, String> metaMap) throws Exception;
}
