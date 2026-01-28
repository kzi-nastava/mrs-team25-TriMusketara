// Interface for when a user wants to change his password
export interface ChangePasswordRequest {
    id: number;
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
}