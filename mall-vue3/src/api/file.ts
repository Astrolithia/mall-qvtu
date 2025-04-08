import { post } from '/@/utils/http/axios';

enum URL {
    uploadImage = '/api/file/upload/image',
    uploadVideo = '/api/file/upload/video',
}

/**
 * 上传图片（富文本编辑器使用）
 * @param file 图片文件
 * @returns Promise
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
 * 上传视频（富文本编辑器使用）
 * @param file 视频文件
 * @returns Promise
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