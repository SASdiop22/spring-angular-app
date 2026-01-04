export interface User {
    id: number;
    username: string;
    email: string;
    role: 'ADMIN' | 'RECRUITER' | 'CANDIDATE' | 'EMPLOYEE';
    firstName?: string;
    lastName?: string;
}
