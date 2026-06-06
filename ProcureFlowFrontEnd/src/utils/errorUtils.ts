import axios from 'axios';

export const getApiErrorMessage = (error: unknown, fallback: string) => {
  if (axios.isAxiosError(error)) {
    const responseData = error.response?.data;
    const status = error.response?.status;

    if (typeof responseData === 'string') {
      return responseData;
    }

    if (typeof responseData?.message === 'string') {
      return responseData.message;
    }

    if (typeof responseData?.error === 'string') {
      return responseData.error;
    }

    if (responseData && typeof responseData === 'object') {
      const fieldMessages = Object.entries(responseData)
        .filter(([, value]) => typeof value === 'string')
        .map(([field, message]) => `${field}: ${message}`)
        .join(', ');

      if (fieldMessages) {
        return fieldMessages;
      }
    }

    if (!error.response) {
      return 'Cannot reach the backend API. Check that Spring Boot is running on http://localhost:8081 and CORS allows http://127.0.0.1:5173.';
    }

    if (status) {
      return `${fallback} Backend returned HTTP ${status}.`;
    }
  }

  return fallback;
};
