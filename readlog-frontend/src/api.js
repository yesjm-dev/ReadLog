// api.js
const API_BASE_URL = 'http://localhost:8080';

const getHeaders = () => {
  const token = localStorage.getItem('accessToken');
  const headers = {
    'Content-Type': 'application/json',
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  return headers;
};

const handleResponse = async (response) => {
  // 401 에러면 로그아웃
  if (response.status === 401) {
    localStorage.removeItem('accessToken');
    window.location.href = '/login';
    throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
  }
  
  if (!response.ok) {
    throw new Error(`API 요청 실패: ${response.status}`);
  }
  
  // 204 No Content
  if (response.status === 204) {
    return null;
  }
  
  return response.json();
};

export const api = {
  // GET 요청
  get: async (url) => {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      method: 'GET',
      headers: getHeaders()
    });
    return handleResponse(response);
  },
  
  // POST 요청
  post: async (url, data) => {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      method: 'POST',
      headers: getHeaders(),
      body: JSON.stringify(data)
    });
    return handleResponse(response);
  },
  
  // PUT 요청
  put: async (url, data) => {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify(data)
    });
    return handleResponse(response);
  },
  
  // DELETE 요청
  delete: async (url) => {
    const response = await fetch(`${API_BASE_URL}${url}`, {
      method: 'DELETE',
      headers: getHeaders()
    });
    return handleResponse(response);
  }
};

// 책 검색
export const searchBooks = async (query) => {
  try {
    const token = localStorage.getItem('accessToken');
    const response = await fetch(
      `${API_BASE_URL}/api/books/search?query=${encodeURIComponent(query)}`,
      {
        headers: {
          ...(token && { 'Authorization': `Bearer ${token}` })
        }
      }
    );
    if (!response.ok) throw new Error('검색 실패');
    return await response.json();
  } catch (error) {
    console.error('API 오류:', error);
    throw error;
  }
};

export const logout = () => {
  localStorage.removeItem('accessToken');
};