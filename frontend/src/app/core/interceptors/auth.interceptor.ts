import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { API_URL } from '../config/api.config';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.token();
  const request = token && req.url.startsWith(API_URL) ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
  return next(request).pipe(catchError((error: HttpErrorResponse) => {
    if (error.status === 401 && !req.url.endsWith('/api/auth/login')) auth.logout();
    if (error.status === 403) void router.navigate(['/sin-acceso']);
    return throwError(() => error);
  }));
};
