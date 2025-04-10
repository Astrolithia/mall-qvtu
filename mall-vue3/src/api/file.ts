/**
 * 文件上传 API 模块
 * 
 * 该模块提供文件上传相关的API请求方法，包括图片和视频的上传功能。
 * 主要用于富文本编辑器中的媒体文件上传，支持表单数据提交。
 * 所有方法返回Promise对象，支持异步调用和错误处理。
 * 
 * @module api/file
 * @author Administrator
 * @version 1.0
 * @date 2024-03-27
 */

import { post } from '/@/utils/http/axios';

/**
 * API路径枚举
 * 
 * 定义所有文件上传相关API的URL路径，便于统一管理和维护
 */
enum URL {
    uploadImage = '/api/file/upload/image',
    uploadVideo = '/api/file/upload/video',
}

/**
 * 上传图片
 * 
 * @description 上传图片文件到服务器，主要用于富文本编辑器中插入图片
 * @param {File} file - 要上传的图片文件对象
 * @returns {Promise<any>} 返回包含上传结果和图片URL的Promise
 */
export const uploadImageApi = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return post<any>({
        url: URL.uploadImage,
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' }
    });
};

/**
 * 上传视频
 * 
 * @description 上传视频文件到服务器，主要用于富文本编辑器中插入视频
 * @param {File} file - 要上传的视频文件对象
 * @returns {Promise<any>} 返回包含上传结果和视频URL的Promise
 */
export const uploadVideoApi = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return post<any>({
        url: URL.uploadVideo,
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data;charset=utf-8' }
    });
}; 