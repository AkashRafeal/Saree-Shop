import { create } from 'zustand';

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role?: string;
  roles?: string[];
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
      const normalizedRole = user.role || (user.roles?.includes('ROLE_ADMIN') ? 'ROLE_ADMIN' : 'ROLE_CUSTOMER');
      const normalizedUser = { ...user, role: normalizedRole };
      localStorage.setItem('sareeaura_token', token);
      localStorage.setItem('sareeaura_user', JSON.stringify(normalizedUser));
      set({ user: normalizedUser, token, isAuthenticated: true });
    },

    logout: () => {
      localStorage.removeItem('sareeaura_token');
      localStorage.removeItem('sareeaura_user');
      set({ user: null, token: null, isAuthenticated: false });
    },
  };
});
