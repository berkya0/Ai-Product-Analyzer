import { useState } from 'react';
import { setProductFollowing } from '../services/dashboardService';

export function useToggleFollow() {
   
    const [isToggling, setIsToggling] = useState(false);

   
    const toggle = async (productId, currentIsFollowing, onSuccess) => {
        try {
            setIsToggling(true);
            const nextStatus = !currentIsFollowing;
            
            await setProductFollowing(productId, nextStatus);
            
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