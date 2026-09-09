import { create } from 'zustand';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ROLE_ADMIN' | 'ROLE_CUSTOMER';
}

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (user: User, token: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => {
  const savedToken = localStorage.getItem('sareeaura_token');
  const savedUser = localStorage.getItem('sareeaura_user');

  return {
    user: savedUser ? JSON.parse(savedUser) : null,
    token: savedToken,
    isAuthenticated: !!savedToken,

    login: (user: User, token: string) => {
      localStorage.setItem('sareeaura_token', token);
      localStorage.setItem('sareeaura_user', JSON.stringify(user));
      set({ user, token, isAuthenticated: true });
    },

    logout: () => {
      localStorage.removeItem('sareeaura_token');
      localStorage.removeItem('sareeaura_user');
      set({ user: null, token: null, isAuthenticated: false });
    },
  };
});
