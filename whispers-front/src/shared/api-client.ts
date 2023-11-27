import { AsyncData } from './async-data';
import { AsyncOperationResult } from './async-operation';

export async function apiGet<T>(url: string): Promise<AsyncData<T>> {
  try {
    const resp = await fetch(`${import.meta.env.VITE_API_BASE || '/api'}${url}`, {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('username')}`,
      }
    });
    if (resp.status === 200) {
      const data = await resp.json();
      return { status: 'loaded', data };
    } else {
      const text = await resp.text();
      console.error('api-client#get: ', resp.status, text);
      return { status: 'error' };
    }
  } catch (err: any) {
    console.error('api-client#get: ', err);
    return { status: 'error' };
  }
}

export async function apiPost<T>(url: string, payload: any): Promise<AsyncOperationResult<T>> {
  try {
    const resp = await fetch(`${import.meta.env.VITE_API_BASE || '/api'}${url}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('username')}`,
      },
      body: JSON.stringify(payload),
    });
    if (resp.status === 200 || resp.status === 201) {
      const data = await resp.json();
      return { status: 'success', data };
    } else {
      const text = await resp.text();
      console.error('api-client#post: ', resp.status, text);
      return { status: 'error' };
    }
  } catch (err: any) {
    console.error('api-client#post: ', err);
    return { status: 'error' };
  }
}
