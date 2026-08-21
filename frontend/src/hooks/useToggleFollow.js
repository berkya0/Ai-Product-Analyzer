import { useState } from 'react';
import { setProductFollowing } from '../services/dashboardService';

export function useToggleFollow() {
   
    const [isToggling, setIsToggling] = useState(false);

    // onSuccess parametresi, işlem başarılı olursa state'i güncellemek için kullanılır
    const toggle = async (productId, currentIsFollowing, onSuccess) => {
        try {
            setIsToggling(true);
            const nextStatus = !currentIsFollowing;
            
            // Backend'e isteği atıyoruz
            await setProductFollowing(productId, nextStatus);
            
            // İstek başarılı olduysa, sayfanın kendi state'ini güncellemesine izin veriyoruz
            if (onSuccess) {
                onSuccess(nextStatus);
            }
        } catch (error) {
            console.error("Takip işlemi başarısız:", error);
            alert("Takip durumu güncellenirken bir hata oluştu.");
        } finally {
            setIsToggling(false);
        }
    };

    return { toggle, isToggling };
}