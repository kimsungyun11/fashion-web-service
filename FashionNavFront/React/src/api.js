// import axios from "axios";

// const api = axios.create({
//     baseURL: "http://localhost:8080/api",// Spring Boot 서버 URL
//     headers: {
//         "Content-Type": "application/json",
//     },
// });

// // 요청 인터셉터 설정
// api.interceptors.request.use(
//     (config) => {
//         const token = localStorage.getItem("token");
//         if (token) {
//             config.headers.Authorization = `Bearer ${token}`;
//         }
//         return config;
//     },
//     (error) => Promise.reject(error)
// );

// // 응답 인터셉터 설정
// api.interceptors.response.use(
//     (response) => response,
//     async (error) => {
//         const originalRequest = error.config;
//         console.log("in error");
//         if (error.response && error.response.status === 401 && !originalRequest._retry) {
//             originalRequest._retry = true;
//             try {
//                 const refreshToken = localStorage.getItem("refreshToken");
//                 console.log("Stored refreshToken:", refreshToken);
//                 if (!refreshToken) {
//                     throw new Error("No refresh token found");
//                 }
//                 const response = await axios.post("http://localhost:8080/api/users/refresh", null, {
//                     headers: { Authorization: `Bearer ${refreshToken}` },
//                 });
//                 const newAccessToken = response.data.body.accessToken;
//                 localStorage.setItem("token", newAccessToken);
//                 originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
//                 return api(originalRequest);
//             } catch (err) {
//                 console.error("Refresh token expired or invalid");
//                 localStorage.removeItem("token");
//                 localStorage.removeItem("refreshToken");
//                 window.location.href = "/login"; // 로그인 페이지로 리디렉션
//                 return Promise.reject(err);
//             }
//         }
//         return Promise.reject(error);
//     }
// );

// export default api;

import axios from "axios";

const api = axios.create({
    baseURL: "https://port-0-fashion-web-service-m45lcyj12bc02bb6.sel4.cloudtype.app/api",
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const refreshToken = localStorage.getItem("refreshToken");
                console.log("Stored refreshToken:", refreshToken);
                if (!refreshToken) {
                    throw new Error("No refresh token found");
                }
                const response = await axios.post("https://port-0-fashion-web-service-m45lcyj12bc02bb6.sel4.cloudtype.app/api/users/refresh", null, {
                    headers: { Authorization: `Bearer ${refreshToken}` },
                });
                const newAccessToken = response.data.accessToken; // 백엔드 응답 구조에 따라 이 부분을 수정할 수 있습니다
                localStorage.setItem("token", newAccessToken);
                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                return api(originalRequest);
            } catch (err) {
                console.error("Refresh token expired or invalid", err);
                localStorage.removeItem("token");
                localStorage.removeItem("refreshToken");
                window.location.href = "/login"; // 로그인 페이지로 리디렉션
                return Promise.reject(err);
            }
        }
        return Promise.reject(error);
    }
);

export default api;