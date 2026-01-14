export interface User {
  id: number;
  username: string;
  nom?: string;
  prenom?: string;
  email?: string;
  telephone?: string;
  userType?: string;
  roles: string[];
  employeProfileId?: number;
}

export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
}

export interface DecodedToken {
  sub: string;
  exp: number;
  iat: number;
  roles?: string[];
}
